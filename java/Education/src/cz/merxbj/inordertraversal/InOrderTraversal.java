/*
 * InOrderTraversal
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.merxbj.inordertraversal;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class InOrderTraversal {

    public static void main(String[] args) {
        Node<String> root = new Node<String>("A");
        root.setLeft(new Node<String>("B"));
        root.getLeft().setLeft(new Node<String>("D"));
        root.getLeft().setRight(new Node<String>("E"));
        root.setRight(new Node<String>("C"));
        root.getRight().setRight(new Node<String>("F"));

        Tree<String> tree = new Tree<String>();
        tree.setRoot(root);

        tree.inOrder(new NodePrinter());
    }

    public static class Tree<E> {

        private Node<E> root;

        public void inOrder(NodeAction a) {
            inOrder(a, root);
        }

        protected void inOrder(NodeAction a, Node<E> r) {
            if (r != null) {
                inOrder(a, r.left);
                a.act(r);
                inOrder(a, r.right);
            }
        }

        public void setRoot(Node<E> root) {
            this.root = root;
        }

        public Node<E> getRoot() {
            return this.root;
        }

    }

    public static class Node<E> {

        public Node<E> left;
        public Node<E> right;
        public Node<E> parent;
        public E value;

        public Node(E value) {
            this.value = value;
        }

        public Node<E> getLeft() {
            return left;
        }

        public void setLeft(Node<E> left) {
            this.left = left;
            left.parent = this;
        }

        public Node<E> getRight() {
            return right;
        }

        public void setRight(Node<E> right) {
            this.right = right;
            right.parent = this;
        }

        @Override
        public String toString() {
            return value.toString();
        }

    }

    public static interface NodeAction {
        public void act(Node<?> value);
    }

    public static class NodePrinter implements NodeAction {

        @Override
        public void act(Node<?> node) {
            System.out.println(node);
        }
        
    }

}
