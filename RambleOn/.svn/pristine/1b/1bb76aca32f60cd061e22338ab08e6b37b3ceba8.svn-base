package ramble_on_events;

import java.util.Collections;
import java.util.LinkedList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import ramble_on.RambleOn;

/**
 * This handler responds to interactions with the world tree. Should the user
 * select a tree node, which represents a region, the region should be loaded
 * into the region editor.
 *
 * @author Richard McKenna Debugging Enterprises
 * @version 1.0
 */
public class WorldTreeHandler implements TreeSelectionListener {
    // THE HANDLER WILL NEED TO CHANGE THE
    // GUI IN ITS RESPONSE

    private RambleOn game;
    private DefaultMutableTreeNode selectedNode;
    private LinkedList<DefaultMutableTreeNode> list;

    /**
     * Constructor, it will store the app's gui for later.
     *
     * @param initApp This app will be needed to respond to the tree
     * interactions.
     */
    public WorldTreeHandler(RambleOn initGmae) {
        // KEEP IT FOR LATER
        game = initGmae;
        list = new LinkedList();
    }

    /**
     * This method is called in response to the user clicking on one of the
     * regions in the world tree.
     *
     * @param tse Event object that contains information about which tree node
     * (and thus region) was selected by the user.
     */
    @Override
    public void valueChanged(TreeSelectionEvent tse) {
        // GET THE SELECTED NODE
        String selectedRegion = "";
        selectedNode = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
        if (selectedNode == game.root) {
            selectedRegion = (String) selectedNode.getUserObject();
            game.clearRegionStats();
            game.displayRegionStats(selectedRegion);
        } else {
            DefaultMutableTreeNode selectedParentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            if (selectedParentNode != null) {
                while (selectedParentNode != game.root) {
                    list.add(selectedParentNode);
                    selectedParentNode = (DefaultMutableTreeNode) selectedParentNode.getParent();
                }
            }
            list.add(game.root);
            Collections.reverse(list);
            list.add(selectedNode);
            for (int i = 0; i < list.size(); i++) {
                if (i != list.size() - 1) {
                    selectedRegion = selectedRegion + ((String) (list.get(i).getUserObject())) + "_";
                } else {
                    selectedRegion = selectedRegion + ((String) (list.get(i).getUserObject()));
                }
            }
            game.clearRegionStats();
            game.displayRegionStats(selectedRegion);
            list.clear();
        }
    }
}