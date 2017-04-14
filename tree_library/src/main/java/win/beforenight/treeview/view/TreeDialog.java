package win.beforenight.treeview.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import win.beforenight.treeview.R;
import win.beforenight.treeview.adapter.BaseAdapterEx3;
import win.beforenight.treeview.entity.TreeNode;

public class TreeDialog extends Dialog
{
    private final List<TreeNode> mItems = new ArrayList<>();
    private final List<TreeNode> mSelectedNodeList = new ArrayList<>();
    private final CharSequence mTitle;
    private final OnTreeNodeClickListener mTreeNodeClickListener;
    private ListView mListView;
    private BaseAdapterEx3<TreeNode> mInnerAdapter;
    private RadioGroup mSelectedNodeListGroupView;
    private TreeNode mSelectedLeafNode;

    public TreeDialog(@NonNull final Context context, @NonNull final CharSequence title, @NonNull final List<TreeNode> items, @NonNull final OnTreeNodeClickListener listener)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        mTitle = title;

        if (items.size() > 0)
            mItems.addAll(items);

        mTreeNodeClickListener = listener;
    }

    private RadioButton getNodeButton()
    {
        final RadioButton view = (RadioButton) getLayoutInflater().inflate(R.layout.tree_dialog_nav_btn, mSelectedNodeListGroupView, false);
        view.setChecked(false);
        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
            {
                if (!isChecked)
                    return;

                //noinspection unchecked
                TreeNode item = (TreeNode) buttonView.getTag();

                //最后一项:请选择
                if (item == null)
                {
                    final TreeNode lastSelectedNode = mSelectedNodeList.get(mSelectedNodeList.size() - 1);
                    //noinspection unchecked
                    mInnerAdapter.setOriginalItems(lastSelectedNode.getChilds());

                    mInnerAdapter.refresh();

                    //当没有 已选择的叶子节点时（已经选择过，下次准备再修改时），需要强制滚动到Top，不然因为重用的ListView，会导致滚动条没有
                    if (mSelectedLeafNode == null)
                        smoothScrollToPosition(0);
                    else
                        smoothScrollToPosition(mInnerAdapter.getOriginaItems().indexOf(mSelectedLeafNode));
                }
                //当点击的是根节点时,
                else if (item.getParent() == null)
                {
                    mInnerAdapter.setOriginalItems(mItems);

                    mInnerAdapter.refresh();

                    smoothScrollToPosition(mInnerAdapter.getOriginaItems().indexOf(item));
                }
                //否则显示上级节点的子节点(当前节点所在其中的Child列表)
                else
                {
                    //noinspection unchecked
                    mInnerAdapter.setOriginalItems(item.getParent().getChilds());

                    mInnerAdapter.refresh();

                    smoothScrollToPosition(mInnerAdapter.getOriginaItems().indexOf(item));
                }
            }
        });
        return view;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tree_dialog);

        //点击半透明区域关闭对话框
        findViewById(R.id.translucent_area).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                dismiss();
            }
        });

        //标题
        ((TextView) findViewById(android.R.id.title)).setText(mTitle);

        //关闭对话框按钮
        findViewById(android.R.id.closeButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                dismiss();
            }
        });

        //已选择的节点 导航条
        mSelectedNodeListGroupView = (RadioGroup) findViewById(R.id.layout_selected_node_list);

        //当前节点的 可选择 列表.
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setAdapter(mInnerAdapter = new BaseAdapterEx3<TreeNode>(getContext(), R.layout.tree_dialog_list_item, mItems)
        {
            @Override
            public void convertView(final ViewHolder viewHolder, final TreeNode item)
            {
                viewHolder.getCheckedTextView(android.R.id.text1).setText(item.getName());

                viewHolder.getCheckedTextView(android.R.id.text1).setChecked(mSelectedNodeList.contains(item) || item.equals(mSelectedLeafNode));

                //当此节点是叶子节点时。
                if (item.getChilds() == null || item.getChilds().size() == 0)
                    viewHolder.getCheckedTextView(android.R.id.text1).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                else
                    viewHolder.getCheckedTextView(android.R.id.text1).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_indicator, 0);
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListenerEx<TreeNode>()
        {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id, final TreeNode item)
            {
                //当此节点是叶子节点时，直接触发回调。
                if (item.getChilds() == null || item.getChilds().size() == 0)
                {
                    mSelectedLeafNode = item;

                    mTreeNodeClickListener.onTreeNodeClick(item);

                    dismiss();
                    return;
                }
                //将 当前点击的节点 添加到导航条
                refreshNodeNavBar(item);

                //展开当前点击的所有子节点.
                //noinspection unchecked
                mInnerAdapter.setOriginalItems(item.<TreeNode>getChilds());
                mInnerAdapter.refresh();
            }
        });
    }

    private void refreshNodeNavBar(final TreeNode item)
    {
        mSelectedNodeList.clear();
        TreeNode tmpT = item;
        //noinspection unchecked
        do
        {
            mSelectedNodeList.add(0, tmpT);
        }
        while ((tmpT = tmpT.getParent()) != null);

        //mSelectedNodeListGroupView.getLayoutTransition();

        mSelectedNodeListGroupView.clearCheck();
        mSelectedNodeListGroupView.removeAllViews();

        for (TreeNode t : mSelectedNodeList)
        {
            final RadioButton nodeButton = getNodeButton();
            //nodeButton.setId(?);//不用手动设置ID，不然ID设置的格式不正确，就会导致RadioGroup的互斥效果不生效。
            nodeButton.setText(t.getName());
            nodeButton.setTag(t);

            mSelectedNodeListGroupView.addView(nodeButton);
        }

        final RadioButton nodeButton = getNodeButton();
        nodeButton.setText("请选择");
        nodeButton.setTag(null);
        mSelectedNodeListGroupView.addView(nodeButton);

        //始终选中最后一个View
        mSelectedNodeListGroupView.check(mSelectedNodeListGroupView.getChildAt(mSelectedNodeListGroupView.getChildCount() - 1).getId());
    }

    /**
     * @param selectedLeafNode 已选择则节点，必须和当前树节点 items 里的对象内存地址保持一致！否则无法正确恢复已选择树节点路径！
     */
    public void show(@Nullable TreeNode selectedLeafNode)
    {
        show();

        //假如当前对话框是第一次显示的时候,外界就
        if (mSelectedLeafNode == null && selectedLeafNode != null)
        {
            mSelectedLeafNode = selectedLeafNode;

            if (selectedLeafNode.getParent() != null)
                //noinspection unchecked
                refreshNodeNavBar(selectedLeafNode.getParent());
        }
    }

    /**
     * 将当前对象节点自动置于顶部
     */
    private void smoothScrollToPosition(final int position)
    {
        mListView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    mListView.smoothScrollToPositionFromTop(position, 0);
                else
                    mListView.smoothScrollToPosition(position);
            }
        }, 0);
    }

    public interface OnTreeNodeClickListener
    {
        void onTreeNodeClick(TreeNode item);
    }
}