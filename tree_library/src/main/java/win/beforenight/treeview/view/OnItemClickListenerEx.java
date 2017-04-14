package win.beforenight.treeview.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @param <T> 设置BaseAdapterEx T 中 T 的参数保持一致。
 * @author AsionTang
 * @since 2013年9月11日
 */
public abstract class OnItemClickListenerEx<T> implements OnItemClickListener
{
    @SuppressWarnings("unchecked")
    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
    {
        this.onItemClick(parent, view, position, id, (T) parent.getItemAtPosition(position));
    }

    /**
     * 和原生的OnItemClickListener接口一致。只多处了末参数值 ITEM,
     *
     * @param item 自动强制转换了而已，省却了每次要强制转换的麻烦：(T) parent.getItemAtPosition(position)
     */
    public abstract void onItemClick(AdapterView<?> parent, View view, int position, long id, T item);
}