package win.beforenight.treeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;

import win.beforenight.treeview.entity.TreeNode;
import win.beforenight.treeview.view.TreeDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private TreeDialog showTreeDialog;
    private LinkedList<TreeNode> mTreeNodeList = new LinkedList<>();
    private TreeNode selectNode;
    private Button showTreeBtn;

    @Override
    public void onClick(final View v)
    {
        switch (v.getId())
        {
            case R.id.showTreeDialog:
                onShowDialog();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        showTreeBtn = (Button) findViewById(R.id.showTreeDialog);
        showTreeBtn.setOnClickListener(this);

        for (int i = 0; i < 10; i++)
        {
            TreeNode treeNode = new TreeNode();
            treeNode.setName("目录一" + i);
            treeNode.setID("100" + i);
            treeNode.setLevel(0);

            LinkedList<TreeNode> childLinkedList = new LinkedList<>();
            for (int j = 0; j < 10; j++)
            {
                TreeNode childTreeNode = new TreeNode();
                childTreeNode.setName("目录二" + i);
                childTreeNode.setID("200" + i);
                childTreeNode.setLevel(1);
                childTreeNode.setParent(treeNode);

                childLinkedList.add(childTreeNode);
            }
            treeNode.addChilds(childLinkedList);

            mTreeNodeList.add(treeNode);
        }
    }

    private void onShowDialog()
    {
        if (showTreeDialog == null)
            showTreeDialog = new TreeDialog(MainActivity.this, "选择内容", mTreeNodeList, new TreeDialog.OnTreeNodeClickListener()
            {
                @Override
                public void onTreeNodeClick(final TreeNode item)
                {
                    selectNode = item;
                }
            });
        //        showTreeDialog.show();
        showTreeDialog.show(selectNode);
    }
}
