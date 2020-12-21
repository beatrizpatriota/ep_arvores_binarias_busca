public class BuscaBinaria implements Busca {
    Node root;

    @Override
    public void insert(String s) {
        var id = Integer.parseInt(s);
        Node newNode = new Node(id);

        if (root == null) {
            root = newNode;
        } else {
            Node current = root;

            while (true) {
                if (id < current.value) {
                    if (current.left == null) {
                        current.left = newNode;
                        break;
                    } else current = current.left;
                } else {
                    if (current.right == null) {
                        current.right = newNode;
                        break;
                    } else current = current.right;
                }
            }
        }
    }

    @Override
    public boolean find(String a) {
        var id = Integer.parseInt(a);
        var current = root;

        while (current != null) {
            if (id < current.value) current = current.left;
            else if (id > current.value) current = current.right;
            else return true;
        }

        return false;
    }

    private class Node {
        final int value;

        Node left = null;
        Node right = null;

        private Node(int value) {
            this.value = value;
        }
    }
}
