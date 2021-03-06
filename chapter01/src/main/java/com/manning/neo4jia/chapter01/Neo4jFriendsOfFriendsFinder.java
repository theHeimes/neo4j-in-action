package com.manning.neo4jia.chapter01;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.StandardExpander;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aleksa Vukotic
 */
public class Neo4jFriendsOfFriendsFinder implements FriendsOfFriendsFinder {

	private static Logger logger = LoggerFactory.getLogger(Neo4jFriendsOfFriendsFinder.class);

	private final GraphDatabaseService graphDb;

	public Neo4jFriendsOfFriendsFinder(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}

	@SuppressWarnings("deprecation")
	@Transactional
	public Long countFriendsOfFriends(Long userId) {
		
		Long result = 0L;
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		try (Transaction tx = graphDb.beginTx()) {
			Node nodeById = graphDb.getNodeById(userId);
			TraversalDescription traversalDescription = Traversal.description()
					.relationships(Constants.IS_FRIEND_OF, Direction.OUTGOING).evaluator(Evaluators.atDepth(2))
					.uniqueness(Uniqueness.NODE_GLOBAL);
			Iterable<Node> nodes = traversalDescription.traverse(nodeById).nodes();

			for (Node n : nodes) {
				result++;
			}
			stopWatch.stop();
			logger.info("NEO4J: Found {} friends of friends for user {}, took " + stopWatch.getTotalTimeMillis()
					+ " millis.", result, userId);
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	@Transactional
	public Long countFriendsOfFriendsDepth3(Long userId) {
		
		Long result = 0L;

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		try (Transaction tx = graphDb.beginTx()) {
			Node nodeById = graphDb.getNodeById(userId);
			TraversalDescription traversalDescription = Traversal.description()
					.relationships(Constants.IS_FRIEND_OF, Direction.OUTGOING).evaluator(Evaluators.atDepth(3))
					.uniqueness(Uniqueness.NODE_GLOBAL);
			Iterable<Node> nodes = traversalDescription.traverse(nodeById).nodes();

			for (Node n : nodes) {
				result++;
			}
			stopWatch.stop();
			logger.info("NEO4J: Found {} friends of friends depth 3 for user {}, took " + stopWatch.getTotalTimeMillis()
					+ " millis.", result, userId);
		}
		return result;
	}

	@Transactional
	public Long countFriendsOfFriendsDepth4(Long userId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Long result = 0L;
		try (Transaction tx = graphDb.beginTx()) {
			Node nodeById = graphDb.getNodeById(userId);
			@SuppressWarnings("deprecation")
			TraversalDescription traversalDescription = Traversal.description()
					.relationships(Constants.IS_FRIEND_OF, Direction.OUTGOING).evaluator(Evaluators.atDepth(4))
					.uniqueness(Uniqueness.NODE_GLOBAL);
			Iterable<Node> nodes = traversalDescription.traverse(nodeById).nodes();
			for (Node n : nodes) {
				result++;
			}
			stopWatch.stop();
			logger.info("NEO4J: Found {} friends of friends depth 4 for user {}, took " + stopWatch.getTotalTimeMillis()
					+ " millis.", result, userId);
		}
		return result;
	}

	@Transactional
	public Long countFriendsOfFriendsDepth5(Long userId) {
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Long result = 0L;
		try (Transaction tx = graphDb.beginTx()) {
			Node nodeById = graphDb.getNodeById(userId);
			@SuppressWarnings("deprecation")
			TraversalDescription traversalDescription = Traversal.description()
					.relationships(Constants.IS_FRIEND_OF, Direction.OUTGOING).evaluator(Evaluators.atDepth(5))
					.uniqueness(Uniqueness.NODE_GLOBAL);
			Iterable<Node> nodes = traversalDescription.traverse(nodeById).nodes();
			for (Node n : nodes) {
				result++;
			}
			stopWatch.stop();
			logger.info("NEO4J: Found {} friends of friends depth 5 for user {}, took " + stopWatch.getTotalTimeMillis()
					+ " millis.", result, userId);
		}
		return result;

	}

	@SuppressWarnings("deprecation")
	public boolean areConnectedViaFriendsUpToLevel4(Long user1, Long user2) {
		try (Transaction tx = graphDb.beginTx()) {
		return GraphAlgoFactory.shortestPath(
				(RelationshipExpander) StandardExpander.DEFAULT.add(Constants.IS_FRIEND_OF, Direction.OUTGOING), 4)
				.findAllPaths(graphDb.getNodeById(user1), graphDb.getNodeById(user2)).iterator().hasNext();
		} catch( Exception e) {
		return false;
		}
	}
}
