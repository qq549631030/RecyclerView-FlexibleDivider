package com.yqritc.recyclerviewflexibledivider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Vertical divider for Vertical grid
 * Created by huangx on 2017/3/17.
 */

public class GridVerticalDividerItemDecoration extends VerticalDividerItemDecoration {

    protected boolean showDividerOnEdge;

    protected GridVerticalDividerItemDecoration(Builder builder) {
        super(builder);
        showDividerOnEdge = builder.showDividerOnEdge;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            super.onDraw(c, parent, state);
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
            super.onDraw(c, parent, state);
        }
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) {
            return;
        }
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();

        int validChildCount = parent.getChildCount();
        int lastChildPosition = -1;
        for (int i = 0; i < validChildCount; i++) {
            View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);

            if (childPosition < lastChildPosition) {
                // Avoid remaining divider when animation starts
                continue;
            }
            lastChildPosition = childPosition;
            int spanIndex = spanSizeLookup.getSpanIndex(childPosition, spanCount);
            if (showDividerOnEdge) {//draw edge divider if needed
                if (spanIndex % spanCount == 0) {
                    Rect bounds = getEdgeDividerBound(childPosition, parent, child);
                    drawDivider(c, childPosition, parent, bounds);
                }
                if (spanIndex % spanCount == spanCount - 1) {
                    Rect bounds = getEdgeDividerBound(childPosition, parent, child);
                    drawDivider(c, childPosition, parent, bounds);
                }
            }

            if (spanIndex % spanCount == 0) {
                //ignore the first span
                continue;
            }

            Rect bounds = getDividerBound(childPosition, parent, child);
            drawDivider(c, childPosition, parent, bounds);
        }
    }

    @Override
    protected Rect getDividerBound(int position, RecyclerView parent, View child) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return super.getDividerBound(position, parent, child);
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
            return super.getDividerBound(position, parent, child);
        }

        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.top = child.getTop() + mMarginProvider.dividerTopMargin(position, parent) + transitionY;
        bounds.bottom = child.getBottom() - mMarginProvider.dividerBottomMargin(position, parent) + transitionY;
        int dividerSize = getDividerSize(position, parent);
        boolean isReverseLayout = isReverseLayout(parent);
        if (mDividerType == DividerType.DRAWABLE) {
            // set left and right position of divider
            if (isReverseLayout) {
                bounds.left = child.getRight() + params.leftMargin + transitionX;
                bounds.right = bounds.left + dividerSize;
            } else {
                bounds.right = child.getLeft() - params.rightMargin + transitionX;
                bounds.left = bounds.right - dividerSize;
            }
        } else {
            // set center point of divider
            int halfSize = dividerSize / 2;
            if (isReverseLayout) {
                bounds.left = child.getRight() + params.leftMargin + halfSize + transitionX;
            } else {
                bounds.left = child.getLeft() - params.rightMargin - halfSize + transitionX;
            }
            bounds.right = bounds.left;
        }

        if (mPositionInsideItem) {
            if (isReverseLayout) {
                bounds.left -= dividerSize;
                bounds.right -= dividerSize;
            } else {
                bounds.left += dividerSize;
                bounds.right += dividerSize;
            }
        }
        return bounds;
    }


    protected Rect getEdgeDividerBound(int position, RecyclerView parent, View child) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();
        int spanIndex = spanSizeLookup.getSpanIndex(position, spanCount);
        if (spanIndex % spanCount == 0) {
            return getDividerBound(position, parent, child);
        } else {
            //start edge
            Rect bounds = new Rect(0, 0, 0, 0);
            int transitionX = (int) ViewCompat.getTranslationX(child);
            int transitionY = (int) ViewCompat.getTranslationY(child);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            bounds.top = child.getTop() + mMarginProvider.dividerTopMargin(position, parent) + transitionY;
            bounds.bottom = child.getBottom() - mMarginProvider.dividerBottomMargin(position, parent) + transitionY;
            int dividerSize = getDividerSize(position, parent);
            boolean isReverseLayout = isReverseLayout(parent);
            if (mDividerType == DividerType.DRAWABLE) {
                // set left and right position of divider
                if (isReverseLayout) {
                    bounds.right = child.getLeft() - params.leftMargin + transitionX;
                    bounds.left = bounds.right - dividerSize;
                } else {
                    bounds.left = child.getRight() + params.rightMargin + transitionX;
                    bounds.right = bounds.left + dividerSize;
                }
            } else {
                // set center point of divider
                int halfSize = dividerSize / 2;
                if (isReverseLayout) {
                    bounds.left = child.getLeft() - params.leftMargin - halfSize + transitionX;
                } else {
                    bounds.left = child.getRight() + params.rightMargin + halfSize + transitionX;
                }
                bounds.right = bounds.left;
            }

            if (mPositionInsideItem) {
                if (isReverseLayout) {
                    bounds.left += dividerSize;
                    bounds.right += dividerSize;
                } else {
                    bounds.left -= dividerSize;
                    bounds.right -= dividerSize;
                }
            }
            return bounds;
        }
    }

    protected void drawDivider(Canvas c, int position, RecyclerView parent, Rect bounds) {
        switch (mDividerType) {
            case DRAWABLE:
                Drawable drawable = mDrawableProvider.drawableProvider(position, parent);
                drawable.setBounds(bounds);
                drawable.draw(c);
                break;
            case PAINT:
                mPaint = mPaintProvider.dividerPaint(position, parent);
                c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
                break;
            case COLOR:
                mPaint.setColor(mColorProvider.dividerColor(position, parent));
                mPaint.setStrokeWidth(mSizeProvider.dividerSize(position, parent));
                c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
                break;
        }
    }

    @Override
    public void getItemOffsets(Rect rect, View v, RecyclerView parent, RecyclerView.State state) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            super.getItemOffsets(rect, v, parent, state);
            return;
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
            super.getItemOffsets(rect, v, parent, state);
            return;
        }

        int position = parent.getChildAdapterPosition(v);
        setItemOffsets(rect, position, parent);
    }

    @Override
    protected void setItemOffsets(Rect outRect, int position, RecyclerView parent) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            super.setItemOffsets(outRect, position, parent);
            return;
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
            super.setItemOffsets(outRect, position, parent);
            return;
        }

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        if (mPositionInsideItem) {
            outRect.set(left, top, right, bottom);
            return;
        }
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();
        int spanIndex = spanSizeLookup.getSpanIndex(position, spanCount);

        float totalDividerConsume;
        float dividerConsumePerItem;
        int groupIndex = getGroupIndex(position, parent);
        float dividerSize = getDividerSize(groupIndex, parent);
        if (showDividerOnEdge) {
            totalDividerConsume = dividerSize * (spanCount + 1);
            dividerConsumePerItem = totalDividerConsume / spanCount;
            if (spanIndex % spanCount == 0) {// first item
                if (isReverseLayout(parent)) {
                    right += dividerSize;
                    left += dividerConsumePerItem - dividerSize;
                } else {
                    left += dividerSize;
                    right += dividerConsumePerItem - dividerSize;
                }
            } else if (spanIndex % spanCount == spanCount - 1) {//last item
                if (isReverseLayout(parent)) {
                    left += dividerSize;
                    right += dividerConsumePerItem - dividerSize;
                } else {
                    left += dividerConsumePerItem - dividerSize;
                    right += dividerSize;
                }
            } else if (spanIndex % spanCount == 1) {//second item
                if (isReverseLayout(parent)) {
                    left += (dividerConsumePerItem - dividerSize) * 2;
                    right += dividerSize * 2 - dividerConsumePerItem;
                } else {
                    left += dividerSize * 2 - dividerConsumePerItem;
                    right += (dividerConsumePerItem - dividerSize) * 2;
                }
            } else if (spanIndex % spanCount == spanCount - 2) {//second item from end
                if (isReverseLayout(parent)) {
                    left += dividerSize * 2 - dividerConsumePerItem;
                    right += (dividerConsumePerItem - dividerSize) * 2;
                } else {
                    left += (dividerConsumePerItem - dividerSize) * 2;
                    right += dividerSize * 2 - dividerConsumePerItem;
                }
            } else {
                left += dividerConsumePerItem / 2;
                right += dividerConsumePerItem / 2;
            }
        } else {
            totalDividerConsume = dividerSize * (spanCount - 1);
            dividerConsumePerItem = totalDividerConsume / spanCount;
            if (spanIndex % spanCount == 0) {// first item
                if (isReverseLayout(parent)) {
                    left += dividerConsumePerItem;
                } else {
                    right += dividerConsumePerItem;
                }
            } else if (spanIndex % spanCount == spanCount - 1) {//last item
                if (isReverseLayout(parent)) {
                    right += dividerConsumePerItem;
                } else {
                    left += dividerConsumePerItem;
                }
            } else if (spanIndex % spanCount == 1) {//second item
                if (isReverseLayout(parent)) {
                    left += dividerConsumePerItem * 2 - dividerSize;
                    right += dividerSize - dividerConsumePerItem;
                } else {
                    left += dividerSize - dividerConsumePerItem;
                    right += dividerConsumePerItem * 2 - dividerSize;
                }
            } else if (spanIndex % spanCount == spanCount - 2) {//second item from end
                if (isReverseLayout(parent)) {
                    left += dividerSize - dividerConsumePerItem;
                    right += dividerConsumePerItem * 2 - dividerSize;
                } else {
                    left += dividerConsumePerItem * 2 - dividerSize;
                    right += dividerSize - dividerConsumePerItem;
                }
            } else {
                left += dividerConsumePerItem / 2;
                right += dividerConsumePerItem / 2;
            }
        }
        outRect.set(left, top, right, bottom);
    }

    @Override
    protected int getLastDividerOffset(RecyclerView parent) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return super.getLastDividerOffset(parent);
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
            return super.getLastDividerOffset(parent);
        }
        return 1;
    }

    @Override
    protected boolean wasDividerAlreadyDrawn(int position, RecyclerView parent) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return super.wasDividerAlreadyDrawn(position, parent);
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
            return super.wasDividerAlreadyDrawn(position, parent);
        }
        return false;
    }

    public static class Builder extends VerticalDividerItemDecoration.Builder {
        private boolean showDividerOnEdge;

        public Builder(Context context) {
            super(context);
        }

        public Builder showDividerOnEdge() {
            showDividerOnEdge = true;
            return this;
        }

        @Override
        public GridVerticalDividerItemDecoration build() {
            return new GridVerticalDividerItemDecoration(this);
        }
    }
}
