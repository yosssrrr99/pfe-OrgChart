package com.hracces.openhr.entities;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


    public class TreeNode {
        private String id;
        private HashSet<String> nameEmployees;
        private String nameManager;
        private List<TreeNode> children;

        public TreeNode(String id) {
            this.id = id;
            this.children = new ArrayList<>();
        }

        public String getId() {
            return id;
        }

        public HashSet<String> getNameEmployees() {
            return nameEmployees;
        }

        public void setNameEmployees(HashSet<String> nameEmployees) {
            this.nameEmployees = nameEmployees;
        }

        public String getNameManager() {
            return nameManager;
        }

        public void setNameManager(String nameManager) {
            this.nameManager = nameManager;
        }

        public List<TreeNode> getChildren() {
            return children;
        }

        public void addChild(TreeNode child) {
            this.children.add(child);
        }

        public Optional<TreeNode> findChildById(String id) {
            return children.stream().filter(child -> child.getId().equals(id)).findFirst();
        }
}
