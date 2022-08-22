package threeChess.agents;

import threeChess.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MonteCarloAgent extends Agent {
	private String name = "Monte";
	private Colour Player;
	private Node root = null;
	private int turnTime = 500;
	private Board prevView;
	private Board view;

	public MonteCarloAgent() {
	}

	/**
	 * Play a move in the game. The agent is given a Board Object representing the
	 * position of all pieces, the history of the game and whose turn it is. They
	 * respond with a move represented by a pair (two element array) of positions:
	 * the start and the end position of the move.
	 * @param board The representation of the game state.
	 * @return a two element array of Position objects, where the first element is
	 * the current position of the piece to be moved, and the second element
	 * is the position to move that piece to.
	 **/
	public Position[] playMove(Board board) {
		try {
			view = (Board) board.clone();
		} catch (CloneNotSupportedException e) {
		}
		setRoot(board);

		// Performs MCTS for specified time
		for (long stop = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(turnTime); stop > System.nanoTime();) {
			Node current = root;
			try {
				view = (Board) board.clone();
			} catch (CloneNotSupportedException e) {
			}

			// Explores the tree to select the best node
			while (current.numVisit != 0) {
				double best = Double.MIN_VALUE;
				Node bestNode = current;
				for (Node n : current.children) {
					if (n.numVisit == 0) {
						bestNode = n;
						break;
					} else {
						double uct = ucb1(n);
						if (uct > best) {
							bestNode = n;
							best = uct;
						}
					}
				}
				if (current == bestNode)
					break; // catch when assignments fail due to game over node being reached
				current = bestNode;

				// moves view of the board to the current state
				try {
					view.move(current.move[0], current.move[1]);
				} catch (ImpossiblePositionException e) {}

				// expands the node when it is a leaf
				if (current.children.isEmpty() && current.numVisit != 0) {
					expandNode(current);
				}
			}

			// simulates a random game to completion
			current.score = simulate(current);
			current.numVisit++;

			// Propagates result back to the root
			backPropagate(current);
		}

		// Picks the move with the best average
		Node bestNode = root.children.get(0);
		double best = bestNode.score / bestNode.numVisit;
		for (Node child : root.children) {
			double test = child.score / child.numVisit;
			if (test > best) {
				best = test;
				bestNode = child;
			}
		}

		return bestNode.move;
	}

	public String toString() {
		return this.name;
	}

	public void finalBoard(Board finalBoard) {
	}

	// sets the root node based on the board passed to it.
	private void setRoot(Board board) {
		Player = board.getTurn();
		Node temp = new Node(null, Player, null);
		root = temp;
		expandNode(root);
	}

	// calculates the upperconfidence bound to determine exploration and
	// exploitation of the tree
	private double ucb1(Node child) {
		double exploitation = child.score / child.numVisit;
		double exploration = Math.sqrt(2) * Math.sqrt(Math.log(child.parent.numVisit) / child.numVisit);
		return exploitation + exploration;
	}

	// expands node when exploring the tree
	private void expandNode(Node current) {
		Position[] pieces = view.getPositions(current.playerId).toArray(new Position[0]);

		// creates a new node for each possible move at each state
		for (int i = 0; i < pieces.length; i++) {
			Position start = pieces[i];
			Position end = start;
			Piece mover = view.getPiece(start);
			Direction[][] steps = mover.getType().getSteps();
			int reps = mover.getType().getStepReps();
			for (Direction[] step : steps) {// tests for each step
				for (int j = 0; j < reps; j++) {
					try {
						end = view.step(mover, step, end, start.getColour() != end.getColour());
					} catch (ImpossiblePositionException e) {
						continue;
					}
					if (view.isLegalMove(start, end)) {
						try {
							prevView = (Board) view.clone();
						} catch (CloneNotSupportedException e) {
						}
						try {
							view.move(start, end);
						} catch (ImpossiblePositionException e) {
						}
						Node child = new Node(current, view.getTurn(), new Position[] { start, end });
						current.add(child);
						view = prevView;
					}
				}
			}
		}
	}

	// simulates a random game to completion from the current state
	private double simulate(Node node) {
		try {
			prevView = (Board) view.clone();
		} catch (CloneNotSupportedException e) {
		}
		Position[][] pieces = new Position[3][0];
		int i = 0;
		for (Colour player : Colour.values()) {
			pieces[i] = view.getPositions(player).toArray(new Position[0]);
			i++;
		}
		while (!view.gameOver()) {

			Position[] move;
			int capBefore;
			int capAfter;
			switch (view.getTurn()) {
				case BLUE:
					capBefore = view.getCaptured(Colour.BLUE).size();
					move = randomMove(view, pieces[0]);
					try {
						view.move(move[0], move[1]);
					} catch (ImpossiblePositionException e) {
					}
					capAfter = view.getCaptured(Colour.BLUE).size();
					pieces = updatePieces(pieces, move, 0, capBefore != capAfter);
					break;

				case GREEN:
					capBefore = view.getCaptured(Colour.GREEN).size();
					move = randomMove(view, pieces[1]);
					try {
						view.move(move[0], move[1]);
					} catch (ImpossiblePositionException e) {
						System.out.println("move failed");
					}
					capAfter = view.getCaptured(Colour.GREEN).size();
					pieces = updatePieces(pieces, move, 1, capAfter != capBefore);
					break;

				case RED:
					capBefore = view.getCaptured(Colour.RED).size();
					move = randomMove(view, pieces[2]);
					try {
						view.move(move[0], move[1]);
					} catch (ImpossiblePositionException e) {
						System.out.println("move failed");
					}
					capAfter = view.getCaptured(Colour.RED).size();
					pieces = updatePieces(pieces, move, 2, capAfter != capBefore);
					break;

			}
		}
		int playerscore = 0;
		if (view.getWinner() == root.playerId) {
			playerscore = 1;
		} else if (view.getLoser() == root.playerId) {
			playerscore = -1;
		}
		return playerscore;
	}

	// Selects a random move for simulation
	private Position[] randomMove(Board board, Position[] pieces) {
		Position start = pieces[0];
		Position end = pieces[0];
		Random random = new Random();

		while (!board.isLegalMove(start, end)) {
			start = pieces[random.nextInt(pieces.length)];
			Piece mover = board.getPiece(start);
			Direction[][] steps = mover.getType().getSteps();
			Direction[] step = steps[random.nextInt(steps.length)];
			int reps = 1 + random.nextInt(mover.getType().getStepReps());
			end = start;
			try {
				for (int i = 0; i < reps; i++) {
					end = board.step(mover, step, end, start.getColour() != end.getColour());
				}
			} catch (ImpossiblePositionException e) {
			}
		}
		return new Position[] { start, end };
	}

	// Updates the position of pieces when playing a move
	private Position[][] updatePieces(Position[][] pieces, Position[] move, int index, boolean isCapture) {
		Position start = move[0];
		Position end = move[1];
		Position[][] updated = new Position[3][0];
		Position[] toUpdate;
		for (int i = 0; i < 3; i++) {
			toUpdate = new Position[pieces[i].length];
			for (int j = 0; j < pieces[i].length; j++) {
				if (i == index && pieces[i][j].equals(start)) {
					toUpdate[j] = end;
					continue;
				}
				if (isCapture && pieces[i][j].equals(end)) {
					toUpdate = new Position[pieces[i].length - 1];
					for (int k = 0, l = 0; k < pieces[i].length; k++) {
						if (k == j)
							continue;
						toUpdate[l++] = pieces[i][k];
					}
					break;
				}
				toUpdate[j] = pieces[i][j];
			}
			updated[i] = toUpdate;
		}

		return updated;
	}

	// updates the parent state to the root state.
	private void backPropagate(Node node) {
		Node current = node;
		double result = node.score;
		while (!current.equals(root)) {
			Node parent = current.parent;
			parent.score += result;
			parent.numVisit++;
			current = parent;
		}
	}

	// Data structure of a node in the Tree holding the board at that state, number
	// of times visited and score
	private class Node {
		Node parent;
		Colour playerId;
		int numVisit;
		double score;
		Position[] move;// saves the move that changes parent state to current state
		List<Node> children;

		private Node(Node parent, Colour player, Position[] move) {
			this.parent = parent;
			playerId = player;
			numVisit = 0;
			score = 0;
			this.move = move;
			children = new ArrayList<Node>();
		}

		// adds child to child list
		private void add(Node child) {
			children.add(child);
		}

	}

}