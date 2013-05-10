package org.glowacki.core.astar;

/**
 * Adapted from http://memoization.com/2008/11/30/a-star-algorithm-in-java/
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Find a path.
 */
public abstract class PathFinder
{
    /**
     * Create a temporary copy of a node
     *
     * @param node node being copied
     *
     * @return temporary copy
     */
    public abstract INode createTempNode(INode node);

    private static INode findBestPassThrough(List<INode> list, INode goal)
        throws PathException
    {
        INode best = null;
        for (INode node : list) {
            if (best == null ||
                node.getPassThrough(goal) < best.getPassThrough(goal))
            {
                best = node;
            }
        }

        return best;
    }

    /**
     * Find the best path from <tt>start</tt> to <tt>goal</tt>.
     *
     * @param start starting node
     * @param goal target node
     *
     * @return list of nodes indicating the path
     *
     * @throws PathException if there is a problem
     */
    public List<INode> findBestPath(INode start, INode goal)
        throws PathException
    {
        List<INode> opened = new ArrayList<INode>();
        List<INode> closed = new ArrayList<INode>();

        for (INode adjacency : getAdjacencies(start)) {
            adjacency.setParent(start);
            if (!adjacency.isStart()) {
                opened.add(adjacency);
            }
        }
        closed.add(start);

        boolean found = false;
        while (!found && opened.size() > 0) {
            INode best = findBestPassThrough(opened, goal);
            opened.remove(best);
            closed.add(best);
            if (best.isEnd()) {
                found = true;
                break;
            }

            Set<INode> neighbors = getAdjacencies(best);
            for (INode neighbor : neighbors) {
                if (opened.contains(neighbor)) {
                    INode tmpNode = createTempNode(neighbor);
                    tmpNode.setParent(best);
                    if (tmpNode.getPassThrough(goal) >=
                        neighbor.getPassThrough(goal))
                    {
                        continue;
                    }
                }

                if (closed.contains(neighbor)) {
                    INode tmpNode = createTempNode(neighbor);
                    tmpNode.setParent(best);
                    if (tmpNode.getPassThrough(goal) >=
                        neighbor.getPassThrough(goal))
                    {
                        continue;
                    }
                }

                neighbor.setParent(best);

                opened.remove(neighbor);
                closed.remove(neighbor);
                opened.add(0, neighbor);
            }
        }

        if (!found) {
            return null;
        }

        List<INode> bestList = new ArrayList<INode>();
        INode best = goal;
        while (best != null && !best.isStart()) {
            bestList.add(best);
            best = best.getParent();
        }

        return bestList;
    }

    /**
     * Get all adjacent nodes
     *
     * @param node center node
     *
     * @return set of adjacent nodes
     *
     * @throws PathException if there is a problem
     */
    public abstract Set<INode> getAdjacencies(INode node)
        throws PathException;
}
