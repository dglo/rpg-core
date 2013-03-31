package org.glowacki.core.astar;

/**
 * Adapted from http://memoization.com/2008/11/30/a-star-algorithm-in-java/
 */
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

abstract class PathFinder
{
    private List<INode> opened = new ArrayList<INode>();
    private List<INode> closed = new ArrayList<INode>();

    abstract INode createTempNode(int x, int y);

    private INode findBestPassThrough(INode goal)
    {
        INode best = null;
        for (INode node : opened) {
            if (best == null ||
                node.getPassThrough(goal) < best.getPassThrough(goal))
            {
                best = node;
            }
        }

        return best;
    }

    public List<INode> findBestPath(INode start, INode goal)
    {
        Set<INode> adjacencies =
            new HashSet<INode>(getAdjacencies(start));
        for (INode adjacency : adjacencies) {
            adjacency.setParent(start);
            if (!adjacency.isStart()) {
                opened.add(adjacency);
            }
        }

        boolean found = false;
        while (!found && opened.size() > 0) {
            INode best = findBestPassThrough(goal);
            opened.remove(best);
            closed.add(best);
            if (best.isEnd()) {
                found = true;
                break;
            }

            Set<INode> neighbors = getAdjacencies(best);
            for (INode neighbor : neighbors) {
                if (opened.contains(neighbor)) {
                    INode tmpNode = createTempNode(neighbor.getX(),
                                                   neighbor.getY());
                    tmpNode.setParent(best);
                    if (tmpNode.getPassThrough(goal) >=
                        neighbor.getPassThrough(goal))
                    {
                        continue;
                    }
                }

                if (closed.contains(neighbor)) {
                    INode tmpNode = createTempNode(neighbor.getX(),
                                                   neighbor.getY());
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

        populateBestList(bestList, goal);

        return bestList;
    }

    abstract Set<INode> getAdjacencies(INode node);

    private void populateBestList(List<INode> bestList, INode node)
    {
        bestList.add(node);
        if (!node.getParent().isStart()) {
            populateBestList(bestList, node.getParent());
        }
    }
}
