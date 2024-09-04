package kr.ac.mmu;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class PhOffsetItemDecoration extends RecyclerView.ItemDecoration {

    private int t_padding;
    private int b_padding;
    private int r_padding;
    private int l_padding;

    public PhOffsetItemDecoration(int t_padding, int b_padding, int r_padding, int l_padding) {
        this.t_padding = t_padding;
        this.b_padding = b_padding;
        this.r_padding = r_padding;
        this.l_padding = l_padding;
    }

    @Override
    public void getItemOffsets(Rect a_outRect, View a_view, RecyclerView a_parent, RecyclerView.State a_state) {
        super.getItemOffsets(a_outRect, a_view, a_parent, a_state);
        a_outRect.top = t_padding;
        a_outRect.bottom = b_padding;
        a_outRect.right = r_padding;
        a_outRect.left = l_padding;
    }
}