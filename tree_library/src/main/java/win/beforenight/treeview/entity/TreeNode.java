package win.beforenight.treeview.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNode
{
    private List<TreeNode> mChilds = new ArrayList<>();
    private String mID = "";
    private String mName = "";
    private int mLevel;
    private TreeNode mParentNode;
    private Object mTag;

    /**
     * <H4>"平面"状数据例子如如下:</H4>
     *
     * @param tList            被打成"平面"的一棵树的数据列表(一般将树存储在数据库表中就是平面的)。支持传递任意对象列表,只要保证能够通过 listener
     *                         提取出ID，Name，ParentID三个关键数据即可。
     * @param idAndTreeNodeMap 用于存储 ID 与 TreeNode 之间的映射关系.方便已知选中的 Node 节点 ID
     *                         来获取对应的树节点。传Null时，说明调用者不需要这个映射关系。
     * @param listener         tList 支持传递任意对象列表,只要保证能够通过 listener 提取出ID，Name，ParentID三个关键数据即可。
     * @return 返回真正立体的树形数据结构.
     */
    @NonNull
    public static <T> List<TreeNode> buildTree(@NonNull final List<T> tList, @Nullable final Map<String, TreeNode> idAndTreeNodeMap, @NonNull final OnTreeNodeFieldDataExtractListener<T> listener)
    {
        final List<TreeNode> mRootList = new ArrayList<>();
        final HashMap<String, List<TreeNode>> parentIdAndNodeListMap = new HashMap<>(tList.size());

        //==========================================================================================
        // 1.构建树的第一轮循环,对数据列表进行初步的整理.
        //==========================================================================================
        for (final T t : tList)
        {
            TreeNode node = new TreeNode();
            node.setID(listener.getID(t));
            node.setName(listener.getName(t));
            node.setTag(t);

            //记录所有 ID 与 TreeNode 的关系.
            if (idAndTreeNodeMap != null)
                idAndTreeNodeMap.put(node.getID(), node);

            final String parentId = listener.getParentId(t);

            //获得树的所有根节点（没有父节点的节点）
            if (TextUtils.isEmpty(parentId))
            {
                mRootList.add(node);
                continue;
            }

            //记录此节点(非根节点)的所有子节点.
            List<TreeNode> childLst = parentIdAndNodeListMap.get(parentId);
            if (childLst == null)
                parentIdAndNodeListMap.put(parentId, childLst = new ArrayList<>());
            childLst.add(node);
        }
        //==========================================================================================
        // 2.开始构建树形数据结构.
        //==========================================================================================
        final int level = 0;
        for (final TreeNode node : mRootList)
        {
            node.setLevel(level);

            if (!parentIdAndNodeListMap.containsKey(node.getID()))
                //当前节点:既是"根节点",也是"叶子结点".
                continue;

            //当前节点:存在子节点.所以不是"叶子结点".
            final List<TreeNode> childList = parentIdAndNodeListMap.get(node.getID());

            buildTreeChildNodeList(node, parentIdAndNodeListMap, childList, level + 1);
        }
        return mRootList;
    }

    private static void buildTreeChildNodeList(final TreeNode parentNode, final Map<String, List<TreeNode>> parentIdAndNodeListMap, final List<TreeNode> childList, final int level)
    {
        List<TreeNode> childs = new ArrayList<>(childList.size());
        for (final TreeNode node : childList)
        {
            childs.add(node);

            node.setLevel(level);

            node.setParent(parentNode);

            if (!parentIdAndNodeListMap.containsKey(node.getID()))
                //当前节点:既是"根节点",也是"叶子结点".
                continue;

            //当前节点:存在子节点.所以不是"叶子结点".
            final List<TreeNode> grandchildList = parentIdAndNodeListMap.get(node.getID());

            buildTreeChildNodeList(node, parentIdAndNodeListMap, grandchildList, level + 1);
        }
        parentNode.addChilds(childs);
    }

    /**
     * 获取此节点下的所有子节点的ID（包括它自己的ID）
     *
     * @return Name1|ID1\n
     * Name1.1|ID1.1\n
     * Name1.1.1|ID1.1.1\n
     * Name1.1.2|ID1.1.2\n
     * Name1.2|ID1.2\n
     * Name1.2.1|ID1.2.1\n
     * Name1.2.2|ID1.2.2\n
     * Name1.3|ID1.3\n
     */
    @NonNull
    public static String getAllId(@NonNull final TreeNode item)
    {
        final StringBuilder strAllId = new StringBuilder();
        strAllId.append(item.getName());
        strAllId.append("|");
        strAllId.append(item.getID());
        strAllId.append("\n");

        for (final TreeNode i : item.getChilds())
            strAllId.append(getAllId(i));
        return strAllId.toString();
    }

    /**
     * 显示指定树节点对应的所有节点名称：上海市 - 上海市 - 闵行区
     */
    @NonNull
    public static String getFullTreeNodePathDisplayString(@NonNull final TreeNode item)
    {
        String txt = item.getName();
        TreeNode tmpNode = item;
        while ((tmpNode = tmpNode.getParent()) != null)
        {
            txt = tmpNode.getName() + " - " + txt;
        }
        return txt;
    }

    /**
     * 设置此Node所属的所有子节点
     */
    public void addChilds(@NonNull List<TreeNode> childs)
    {
        this.mChilds.addAll(childs);
    }

    /**
     * 获取此Node所属的所有子节点
     */
    @NonNull
    public List<TreeNode> getChilds()
    {
        return this.mChilds;
    }

    /**
     * 获取当前Node 唯一标识符（当此Node被点击时，可供区分被点击的是谁）
     */
    @NonNull
    public String getID()
    {
        return mID;
    }

    /**
     * 设置当前Node 唯一标识符（当此Node被点击时，可供区分被点击的是谁）
     */
    public void setID(@NonNull String id)
    {
        this.mID = id;
    }

    /**
     * 获取当前Node所属哪个层级；一般从0级（根节点）开始递增。
     */
    public int getLevel()
    {
        return mLevel;
    }

    /**
     * 设置当前Node所在的层级；一般从0级（根节点）开始递增。
     */
    public void setLevel(int level)
    {
        this.mLevel = level;
    }

    /**
     * 获取当前Node 名字
     */
    @NonNull
    public String getName()
    {
        return mName;
    }

    /**
     * 设置当前Node 名字
     */
    public void setName(@NonNull String name)
    {
        this.mName = name;
    }

    /**
     * 获取此Node所属的父节点
     */
    @Nullable
    public TreeNode getParent()
    {
        return mParentNode;
    }

    /**
     * 设置此Node所属的父节点
     */
    public void setParent(@Nullable TreeNode parent)
    {
        mParentNode = parent;
    }

    public Object getTag()
    {
        return mTag;
    }

    public void setTag(final Object tag)
    {
        mTag = tag;
    }

    /**
     */
    public interface OnTreeNodeFieldDataExtractListener<T>
    {
        /**
         */
        @NonNull
        String getID(@NonNull T item);

        /**
         */
        @NonNull
        String getName(@NonNull T item);

        /**
         */
        @NonNull
        String getParentId(@NonNull T item);
    }
}