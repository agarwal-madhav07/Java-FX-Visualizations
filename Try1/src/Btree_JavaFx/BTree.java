package Btree_JavaFx;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ArrayList;
import Cloner.CloneUtils;

//Todo check serializable interface and cloneUtils

class BTNode<E extends Comparable<E>> implements Serializable {

    private int fullNumber;
    private BTNode<E> father;
    private ArrayList<BTNode<E>> children = new ArrayList<BTNode<E>>();
    private ArrayList<E> keys = new ArrayList<>(); //Todo type of araylist

    public BTNode() {
    }

    public BTNode(int order) {
        fullNumber = order - 1;
    }


    public boolean isLastInternalNode() {
        if (keys.size() == 0)
            return false;

        for (BTNode<E> node : children)
            if (node.keys.size() != 0)
                return false;
        return true;
    }


    public BTNode<E> getFather() {
        return father;
    }


    public void setFather(BTNode<E> father) {
        this.father = father;
    }

    public ArrayList<BTNode<E>> getChildren() {
        return children;
    }


    public BTNode<E> getChild(int index) {
        return children.get(index);
    }


    public void addChild(int index, BTNode<E> node) {
        children.add(index, node);
    }


    public void removeChild(int index) {
        children.remove(index);
    }


    public E getKey(int index) {
        return keys.get(index);
    }


    public void addKey(int index, E element) {
        keys.add(index, element);
    }

    public void removeKey(int index) {
        keys.remove(index);
    }

    public ArrayList<E> getKeys() {
        return keys;
    }


    public boolean isFull() {
        return fullNumber == keys.size();
    }


    public boolean isOverflow() {
        return fullNumber < keys.size();
    }


    public boolean isNull() {
        return keys.isEmpty();
    }

    public int getSize() {
        return keys.size();
    }
}




public class BTree<K extends Comparable<K>> implements Serializable {

    private BTNode<K> root = null;
    private int order, index, treeSize;
    private final int halfNumber;
    public final BTNode<K> nullBTNode = new BTNode<K>();

    private LinkedList<BTree<K>> stepTrees = new LinkedList<BTree<K>>();


    public BTree(int order) {
        if (order < 3) {
            try {
                throw new Exception("B-tree's order can not lower than 3");
            } catch (Exception e) {
                e.printStackTrace();
            }
            order = 3;
        }
        this.order = order;
        halfNumber = (order - 1) / 2;
    }


    public boolean isEmpty() {
        return root == null;
    }


    public BTNode<K> getRoot() {
        return root;
    }

    public void setRoot(BTNode<K> root) {
        this.root = root;
    }


    public int getTreeSize() {
        return treeSize;
    }

    public int getHalfNumber() {
        return halfNumber;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public LinkedList<BTree<K>> getStepTrees() {
        return stepTrees;
    }

    public void setStepTrees(LinkedList<BTree<K>> stepTrees) {
        this.stepTrees = stepTrees;
    }


    public int getHeight() {
        if (isEmpty()) {
            return 0;
        } else {
            return getHeight(root);
        }
    }

    public int getHeight(BTNode<K> node) {
        int height = 0;
        BTNode<K> currentNode = node;
        while (!currentNode.equals(nullBTNode)) {
            currentNode = currentNode.getChild(0);
            height++;
        }
        return height;
    }
    //Todo count no. of vertices, currentnode.getchild.size;

    public BTNode<K> getNode(K key) {
        if (isEmpty()) {
            return nullBTNode;
        }
        BTNode<K> currentNode = root;
        while (!currentNode.equals(nullBTNode)) {
            int i = 0;
            while (i < currentNode.getSize()) {
                if (currentNode.getKey(i).equals(key)) {
                    index = i;
                    return currentNode;
                } else if (currentNode.getKey(i).compareTo(key) > 0) {
                    currentNode = currentNode.getChild(i);
                    i = 0;
                } else {
                    i++;
                }
            }
            if (!currentNode.isNull()) {
                currentNode = currentNode.getChild(currentNode.getSize());
            }
        }
        return nullBTNode;
    }


    private BTNode<K> getHalfKeys(K key, BTNode<K> fullNode) {
        int fullNodeSize = fullNode.getSize();

        //Add node to the desired location
        for (int i = 0; i < fullNodeSize; i++) {
            if (fullNode.getKey(i).compareTo(key) > 0) {
                fullNode.addKey(i, key);
                break;
            }
        }
        if (fullNodeSize == fullNode.getSize())
            fullNode.addKey(fullNodeSize, key);

        System.out.println("Insert key into the meeting location");
//		stepMess.add("Insert " + key + " into the meeting position");
        stepTrees.add(CloneUtils.clone(this));
        return getHalfKeys(fullNode);
    }


    private BTNode<K> getHalfKeys(BTNode<K> fullNode) {
        BTNode<K> newNode = new BTNode<K>(order);
        for (int i = 0; i < halfNumber; i++) {
            newNode.addKey(i, fullNode.getKey(0));
            fullNode.removeKey(0);
        }
        return newNode;
    }


    private BTNode<K> getRestOfHalfKeys(BTNode<K> halfNode) {
        BTNode<K> newNode = new BTNode<K>(order);
        int halfNodeSize = halfNode.getSize();
        for (int i = 0; i < halfNodeSize; i++) {
            if (i != 0) {
                newNode.addKey(i - 1, halfNode.getKey(1));
                halfNode.removeKey(1);
            }
            newNode.addChild(i, halfNode.getChild(0));
            halfNode.removeChild(0);
        }
        return newNode;
    }


    private void mergeWithFatherNode(BTNode<K> childNode, int index) {
        childNode.getFather().addKey(index, childNode.getKey(0));
        childNode.getFather().removeChild(index);
        childNode.getFather().addChild(index, childNode.getChild(0));
        childNode.getFather().addChild(index + 1, childNode.getChild(1));
    }


    private void mergeWithFatherNode(BTNode<K> childNode) {
        int fatherNodeSize = childNode.getFather().getSize();
        for (int i = 0; i < fatherNodeSize; i++) {
            if (childNode.getFather().getKey(i).compareTo(childNode.getKey(0)) > 0) {
                mergeWithFatherNode(childNode, i);
                break;
            }
        }
        if (fatherNodeSize == childNode.getFather().getSize()) {
            mergeWithFatherNode(childNode, fatherNodeSize);
        }
        for (int i = 0; i <= childNode.getFather().getSize(); i++)
            childNode.getFather().getChild(i).setFather(childNode.getFather());
    }


    private void setSplitFatherNode(BTNode<K> node) {
        for (int i = 0; i <= node.getSize(); i++)
            node.getChild(i).setFather(node);
    }


    private void processOverflow(BTNode<K> currentNode) {
        BTNode<K> newNode = getHalfKeys(currentNode);
        for (int i = 0; i <= newNode.getSize(); i++) {
            newNode.addChild(i, currentNode.getChild(0));
            currentNode.removeChild(0);
        }
        BTNode<K> originalNode = getRestOfHalfKeys(currentNode);
        currentNode.addChild(0, newNode);
        currentNode.addChild(1, originalNode);
        originalNode.setFather(currentNode);
        newNode.setFather(currentNode);
        setSplitFatherNode(originalNode);
        setSplitFatherNode(newNode);

        System.out.println("Enter the key in the middle of the meeting");

        stepTrees.add(CloneUtils.clone(this));
    }


    public void insert(K key) {
        // If tree is empty
        if (isEmpty()) {
            root = new BTNode<K>(order);
            root.addKey(0, key);
            treeSize++;
            root.setFather(nullBTNode);
            root.addChild(0, nullBTNode);
            root.addChild(1, nullBTNode);

            System.out.println("root inserted");
            stepTrees.add(CloneUtils.clone(this));
            return;
        }

        BTNode<K> currentNode = root;

        while (!currentNode.isLastInternalNode()) {
            int i = 0;
            while (i < currentNode.getSize()) {
                // break if currentNode is leaf
                if (currentNode.isLastInternalNode()) {
                    i = currentNode.getSize();
                } else if (currentNode.getKey(i).compareTo(key) > 0) {
                    currentNode = currentNode.getChild(i);
                    i = 0;
                } else {
                    i++;
                }
            }
            if (!currentNode.isLastInternalNode())
                currentNode = currentNode.getChild(currentNode.getSize());
        }

        // If node is not full then insert non full
        if (!currentNode.isFull()) {
            int i = 0;
            while (i < currentNode.getSize()) {
                //insert any location due to the key> insertKey
                if (currentNode.getKey(i).compareTo(key) > 0) {
                    currentNode.addKey(i, key);
                    currentNode.addChild(currentNode.getSize(), nullBTNode);
                    treeSize++;

                    System.out.println("Insert non full");
                    stepTrees.add(CloneUtils.clone(this));
                    return;
                } else {
                    i++;
                }
            }
            //insert at the end of the node
            currentNode.addKey(currentNode.getSize(), key);
            currentNode.addChild(currentNode.getSize(), nullBTNode);
            treeSize++;

            System.out.println("Insert non full v2");
            stepTrees.add(CloneUtils.clone(this));
        } else {
            // If node is full
            // Insert node into the desired location in do node
            //push 1/2 key + child in node
            BTNode<K> newChildNode = getHalfKeys(key, currentNode);
            for (int i = 0; i < halfNumber; i++) {
                newChildNode.addChild(i, currentNode.getChild(0));
                currentNode.removeChild(0);
            }
            newChildNode.addChild(halfNumber, nullBTNode);

// Lay a half-hybrid, just like that, the current node will be a hybrid of the middle key
// Move up 1 item (used to be the father)
            BTNode<K> originalFatherNode = getRestOfHalfKeys(currentNode);
            currentNode.addChild(0, newChildNode);
            currentNode.addChild(1, originalFatherNode);
            originalFatherNode.setFather(currentNode);
            newChildNode.setFather(currentNode);
            treeSize++;

            System.out.println("Move the key in the middle of nowhere");

            stepTrees.add(CloneUtils.clone(this));

            // If on current, child node cap is higher
            // and node entered on
            if (!currentNode.getFather().equals(nullBTNode)) {
                while (!currentNode.getFather().isOverflow() && !currentNode.getFather().equals(nullBTNode)) {
                    boolean flag = currentNode.getSize() == 1 && !currentNode.getFather().isOverflow();
                    if (currentNode.isOverflow() || flag) {
                        mergeWithFatherNode(currentNode);
                        currentNode = currentNode.getFather();

                        System.out.println("The insert key has been entered into the meeting location");
                        stepTrees.add(CloneUtils.clone(this));

                        // If you come back for a full test, repeat your activities
                        if (currentNode.isOverflow()) {
                            processOverflow(currentNode);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }


    private int findChild(BTNode<K> node) {
        if (!node.equals(root)) {
            BTNode<K> fatherNode = node.getFather();

            for (int i = 0; i <= fatherNode.getSize(); i++) {
                if (fatherNode.getChild(i).equals(node))
                    return i;
            }
        }
        return -1;
    }


    private BTNode<K> balanceDeletedNode(BTNode<K> node) {
        boolean flag;
        int nodeIndex = findChild(node);
        K pair;
        BTNode<K> fatherNode = node.getFather();
        BTNode<K> currentNode;
        if (nodeIndex == 0) {
            currentNode = fatherNode.getChild(1);
            // node (bi xoa) co phai o ngoai cung ben trai ko (index 0)
            flag = true;
        } else {
            currentNode = fatherNode.getChild(nodeIndex - 1);
            flag = false;
        }

        int currentSize = currentNode.getSize();
        if (currentSize > halfNumber) {
            if (flag) {
                pair = fatherNode.getKey(0);
                node.addKey(node.getSize(), pair);
                fatherNode.removeKey(0);
                pair = currentNode.getKey(0);
                currentNode.removeKey(0);
                node.addChild(node.getSize(), currentNode.getChild(0));
                currentNode.removeChild(0);
                fatherNode.addKey(0, pair);
                if (node.isLastInternalNode()) {
                    node.removeChild(0);
                }
//				System.out.println("BA1");
                System.out.println("Case 2a:");
//				stepMess.add(" ");
                stepTrees.add(CloneUtils.clone(this));

            } else {
                pair = fatherNode.getKey(nodeIndex - 1);
                node.addKey(0, pair);
                fatherNode.removeKey(nodeIndex - 1);
                pair = currentNode.getKey(currentSize - 1);
                currentNode.removeKey(currentSize - 1);
                node.addChild(0, currentNode.getChild(currentSize));
                currentNode.removeChild(currentSize);
                fatherNode.addKey(nodeIndex - 1, pair);
                if (node.isLastInternalNode()) {
                    node.removeChild(0);
                }
//				System.out.println("BA2");
                System.out.println("Case 2a:");
//				stepMess.add(" ");
                stepTrees.add(CloneUtils.clone(this));
            }
            return node;
        } else {
            if (flag) {
                currentNode.addKey(0, fatherNode.getKey(0));
                fatherNode.removeKey(0);
                fatherNode.removeChild(0);
                if (root.getSize() == 0) {
                    root = currentNode;
                    currentNode.setFather(nullBTNode);
                }
                if (node.getSize() == 0) {
                    currentNode.addChild(0, node.getChild(0));
                    currentNode.getChild(0).setFather(currentNode);
                }
                for (int i = 0; i < node.getSize(); i++) {
                    currentNode.addKey(i, node.getKey(i));
                    currentNode.addChild(i, node.getChild(i));
                    currentNode.getChild(i).setFather(currentNode);
                }
                // Case 2b.1
                System.out.println("Case 2b: Merging");
//				stepMess.add("Merging");
                stepTrees.add(CloneUtils.clone(this));
            } else {
                currentNode.addKey(currentNode.getSize(), fatherNode.getKey(nodeIndex - 1));
                fatherNode.removeKey(nodeIndex - 1);
                fatherNode.removeChild(nodeIndex);
                if (root.getSize() == 0) {
                    root = currentNode;
                    currentNode.setFather(nullBTNode);
                }
                int currentNodeSize = currentNode.getSize();
                if (node.getSize() == 0) {
                    currentNode.addChild(currentNodeSize, node.getChild(0));
                    currentNode.getChild(currentNodeSize).setFather(currentNode);
                }
                for (int i = 0; i < node.getSize(); i++) {
                    currentNode.addKey(currentNodeSize + i, node.getKey(i));
                    currentNode.addChild(currentNodeSize + i, node.getChild(i));
                    currentNode.getChild(currentNodeSize + i).setFather(currentNode);
                }
                // Case 2b.2
                System.out.println("Case 2b: Merging");
//				stepMess.add("Merging");
                stepTrees.add(CloneUtils.clone(this));
            }
            return fatherNode;
        }
    }


    private BTNode<K> replaceNode(BTNode<K> node) {
        BTNode<K> currentNode = node.getChild(index + 1);
        while (!currentNode.isLastInternalNode()) {
            currentNode = currentNode.getChild(0);
        }

        if (currentNode.getSize() - 1 < halfNumber) {
            // Thay the bang con trai gan nhat (lon nhat)
            currentNode = node.getChild(index);
            int currentNodeSize = currentNode.getSize();
            while (!currentNode.isLastInternalNode()) {
                currentNode = currentNode.getChild(currentNodeSize);
            }
            node.addKey(index, currentNode.getKey(currentNodeSize - 1));
            currentNode.removeKey(currentNodeSize - 1);
            currentNode.addKey(currentNodeSize - 1, node.getKey(index + 1));
            node.removeKey(index + 1);
            index = currentNode.getSize() - 1;
            // Case 3a
            System.out.println("Case 3a: Thay the bang con trai gan nhat (lon nhat)");
//			stepMess.add("Thay the bang con trai gan nhat (lon nhat)");
            stepTrees.add(CloneUtils.clone(this));
        } else {
            // Thay the bang con phai gan nhat (nho nhat)
            node.addKey(index + 1, currentNode.getKey(0));
            currentNode.removeKey(0);
            currentNode.addKey(0, node.getKey(index));
            node.removeKey(index);
            index = 0;
            // Case 3b
            System.out.println("Case 3b: Thay the bang con phai gan nhat (nho nhat)");
//			stepMess.add("Thay the bang con phai gan nhat (nho nhat)");
            stepTrees.add(CloneUtils.clone(this));
        }
        return currentNode;
    }



    /*
     * Case 1: If k is in the node x which is a leaf and x.size -1 >= halfNumber
     * Case 2: If k is in the node x which is a leaf and x.size -1 < halfNumber Case
     * 3: If k is in the node x and x is an internal node (not a leaf)
     */
    public void delete(K key) {
        System.out.println("--------------------------------------\nDelete\n--------------------------------------");
//		stepMess.add("Cay ban dau");
        stepTrees.add(CloneUtils.clone(this));
        // Tim kiem node chua key
        BTNode<K> node = getNode(key);
        BTNode<K> deleteNode = null;
        if (node.equals(nullBTNode))
            return;

        // Neu la root, cay 1 node 1 key -> Xoa luon
        if (node.equals(root) && node.getSize() == 1 && node.isLastInternalNode()) {
            root = null;
            treeSize--;

            System.out.println("Xoa goc");
            stepTrees.add(CloneUtils.clone(this));
        } else {
            boolean flag = true;
            boolean isReplaced = false;
            // TODO: case 3
            if (!node.isLastInternalNode()) {
                node = replaceNode(node);
                deleteNode = node;
                isReplaced = true;
            }

            // Neu xoa lam anh huong den do cao cay
            if (node.getSize() - 1 < halfNumber) {
//				System.out.println("Case 2:");
                // TODO: case 2
                node = balanceDeletedNode(node);
                if (isReplaced) {
                    for (int i = 0; i <= node.getSize(); i++) {
                        for (int j = 0; i < node.getChild(i).getSize(); j++) {
                            if (node.getChild(i).getKey(j).equals(key)) {
                                deleteNode = node.getChild(i);
                                break;
                            }
                        }
                    }
                }
            } else if (node.isLastInternalNode()) {
                // TODO: Case 1
                System.out.println("Case 1: Delete");
                node.removeChild(0);
            }

            while (!node.getChild(0).equals(root) && node.getSize() < halfNumber && flag) {
//				System.out.println("Debug3");
                System.out.println("This is case 3c: Recursively delete");
                if (node.equals(root)) {
                    for (int i = 0; i <= root.getSize(); i++) {
                        if (root.getChild(i).getSize() == 0) {
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    node = balanceDeletedNode(node);
                }
            }

            if (deleteNode == null) {
                // Ktra xem da xoa truoc do chua hay moi chi rebalance/ replace
                node = getNode(key);
            } else {
                node = deleteNode;
            }

            if (!node.equals(nullBTNode)) {
                // Sau khi replace xong thi xoa node di (khi do, node da tro thanh la)
                for (int i = 0; i < node.getSize(); i++) {
                    if (node.getKey(i) == key) {
                        node.removeKey(i);
                    }
                }
                treeSize--;

                System.out.println("Xoa " + key);
                stepTrees.add(CloneUtils.clone(this));
            }
        }
    }
}