package org.zq.util.tree;

public class CatalogTree extends AbstractTree {

	private CatalogTree() {
	}

	//private static CatalogTree instance = null;

//	public static synchronized CatalogTree getInstance() {
//		if (instance == null) {
//			instance = new CatalogTree();
//			instance.reloadCatalogs();
//		}
//		return instance;
//	}

	@Override
	protected TreeNode transform(Object info) {
//		Catalog catalog = (Catalog) info;
        TreeNode node = new TreeNode();
//        node.setNodeId(catalog.getCatalogId());
//        node.setParentId(catalog.getParentId());
//        node.setBindData(catalog);
        return node;
	}
	
//	private void reloadCatalogs() {
//        List nodes = CatalogDAO.getInstance().findAll();
//        super.reload(nodes);
//    }
//
//    public Catalog getCatalogNode(String catalogId) {
//        TreeNode node = super.getTreeNode(catalogId);
//        return node == null ? null : (Catalog) node.getBindData();
//    }

}
