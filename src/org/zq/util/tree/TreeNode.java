package org.zq.util.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections.Predicate;
import org.zq.util.ObjectUtils;

public class TreeNode {
	private AbstractTree tree;
	private TreeNode parent;
	private List<TreeNode> children = new Vector<TreeNode>();
	private List<TreeNode> childrenGroup = new ArrayList<TreeNode>();
	private String nodeId;
	private String parentId;
	private Object bindData;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Object getBindData() {
		return bindData;
	}

	public void setBindData(Object bindData) {
		this.bindData = bindData;
	}

	public AbstractTree getTree() {
		return tree;
	}

	void setTree(AbstractTree tree) {
		this.tree = tree;
	}

	void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public TreeNode getParent() {
		return this.parent;
	}

	public List<TreeNode> getChildren() {
		return this.children;
	}

	public void addChild(TreeNode node) {
		children.add(node);
	}

	/**
	 * get all children, and chilren's children
	 */
	public List<TreeNode> getAllChildren() {
		if (this.childrenGroup.isEmpty()) {
			synchronized (this.tree) {
				for (int i = 0; i < this.children.size(); i++) {
					TreeNode node = (TreeNode) this.children.get(i);
					this.childrenGroup.add(node);
					this.childrenGroup.addAll(node.getAllChildren());
				}
			}
		}
		return this.childrenGroup;
	}

	/**
	 * get all children, and chilren's children
	 */
	public List<TreeNode> getAllChildren(Predicate predicate) {
		List<TreeNode> groups = new ArrayList<TreeNode>();
		fillAllChildren(groups, predicate);
		return groups;
	}

	private void fillAllChildren(List<TreeNode> groups, Predicate predicate) {
		for (int i = 0; i < this.children.size(); i++) {
			TreeNode node = (TreeNode) this.children.get(i);
			if (predicate.evaluate(node)) {
				groups.add(node);
				node.fillAllChildren(groups, predicate);
			}
		}
	}

	/**
	 * get all parents, and parent's parent
	 */
	public List<TreeNode> getParents() {
		List<TreeNode> results = new ArrayList<TreeNode>();
		TreeNode parent = this.getParent();
		while (parent != null) {
			results.add(parent);
			parent = parent.getParent();
		}
		return results;
	}

	/**
	 * A.isMyParent(B) == B is A' parent ? <br>
	 * root.isMyParent(null) == true; <br>
	 * root.isMyParent(*) == false <br>
	 * *.isMyParent(null) == false
	 */
	public boolean isMyParent(String nodeId) {
		TreeNode target = tree.getTreeNode(nodeId);
		TreeNode parent = this.getParent();
		if (parent == null) {
			return target == null;
		} else {
			return parent.equals(target);
		}
	}

	/**
	 * A.isMyAncestor(B) == B is A' ancestor ? <br>
	 * *.isMyAncestor(null) == true;
	 */
	public boolean isMyAncestor(String nodeId) {
		TreeNode target = tree.getTreeNode(nodeId);
		if (target == null)
			return true;

		return target.getAllChildren().contains(this);
	}

	/**
	 * A.isMyBrother(B) == B is A' brother ? <br>
	 * *.isMyBrother(null) == false
	 */
	public boolean isMyBrother(String nodeId) {
		TreeNode target = tree.getTreeNode(nodeId);
		if (target == null)
			return false;

		TreeNode p1 = this.getParent();
		TreeNode p2 = target.getParent();
		return ObjectUtils.equals(p1, p2);
	}
}
