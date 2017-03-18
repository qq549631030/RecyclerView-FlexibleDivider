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
 * Horizontal divider for Horizontal grid
 * Created by huangx on 2017/3/18.
 */

public class GridHorizontalDividerItemDecoration extends HorizontalDividerItemDecoration {

    protected boolean showDividerOnEdge;

    protected GridHorizontalDividerItemDecoration(Builder builder) {
        super(builder);
        showDividerOnEdge = builder.showDividerOnEdge;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            super.onDraw(c, parent, state);
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL) {
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
                if (spanIndex % spanCount == 0 || spanIndex % spanCount == spanCount - 1) {
                    Rect bounds = getEdgeDividerBound(childPosition, parent, child);
                    switch (mDividerType) {
                        case DRAWABLE:
                            Drawable drawable = mDrawableProvider.drawableProvider(childPosition, parent);
                            drawable.setBounds(bounds);
                            drawable.draw(c);
                            break;
                        case PAINT:
                            mPaint = mPaintProvider.dividerPaint(childPosition, parent);
                            c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
                            break;
                        case COLOR:
                            mPaint.setColor(mColorProvider.dividerColor(childPosition, parent));
                            mPaint.setStrokeWidth(mSizeProvider.dividerSize(childPosition, parent));
                            c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
                            break;
                    }
                }
            }

            if (spanIndex % spanCount >= spanCount - 1) {
                //ignore the last span
                continue;
            }

            Rect bounds = getDividerBound(childPosition, parent, child);
            switch (mDividerType) {
                case DRAWABLE:
                    Drawable drawable = mDrawableProvider.drawableProvider(childPosition, parent);
                    drawable.setBounds(bounds);
                    drawable.draw(c);
                    break;
                case PAINT:
                    mPaint = mPaintProvider.dividerPaint(childPosition, parent);
                    c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
                    break;
                case COLOR:
                    mPaint.setColor(mColorProvider.dividerColor(childPosition, parent));
                    mPaint.setStrokeWidth(mSizeProvider.dividerSize(childPosition, parent));
                    c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
                    break;
            }
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
        bounds.left = child.getLeft() + mMarginProvider.dividerLeftMargin(position, parent) + transitionX;
        bounds.right = child.getRight() - mMarginProvider.dividerRightMargin(position, parent) + transitionX;
        int dividerSize = getDividerSize(position, parent);
        boolean isReverseLayout = isReverseLayout(parent);
        if (mDividerType == DividerType.DRAWABLE) {
            // set left and right position of divider
            if (isReverseLayout) {
                bounds.bottom = child.getTop() - params.topMargin + transitionY;
                bounds.top = bounds.bottom - dividerSize;
            } else {
                bounds.top = child.getBottom() + params.bottomMargin + transitionY;
                bounds.bottom = bounds.top + dividerSize;
            }
        } else {
            // set center point of divider
            int halfSize = dividerSize / 2;
            if (isReverseLayout) {
                bounds.top = child.getTop() - params.topMargin - halfSize + transitionY;
            } else {
                bounds.top = child.getBottom() + params.bottomMargin + halfSize + transitionY;
            }
            bounds.bottom = bounds.top;
        }

        if (mPositionInsideItem) {
            if (isReverseLayout) {
                bounds.top += dividerSize;
                bounds.bottom += dividerSize;
            } else {
                bounds.top -= dividerSize;
                bounds.bottom -= dividerSize;
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
            //start edge
            Rect bounds = new Rect(0, 0, 0, 0);
            int transitionX = (int) ViewCompat.getTranslationX(child);
            int transitionY = (int) ViewCompat.getTranslationY(child);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            bounds.left = child.getLeft() + mMarginProvider.dividerLeftMargin(position, parent) + transitionX;
            bounds.right = child.getRight() - mMarginProvider.dividerRightMargin(position, parent) + transitionX;
            int dividerSize = getDividerSize(position, parent);
            boolean isReverseLayout = isReverseLayout(parent);
            if (mDividerType == DividerType.DRAWABLE) {
                // set left and right position of divider
                if (isReverseLayout) {
                    bounds.top = child.getBottom() + params.bottomMargin + transitionY;
                    bounds.bottom = bounds.top + dividerSize;
                } else {
                    bounds.bottom = child.getTop() - params.topMargin + transitionY;
                    bounds.top = bounds.bottom - dividerSize;
                }
            } else {
                // set center point of divider
                int halfSize = dividerSize / 2;
                if (isReverseLayout) {
                    bounds.top = child.getBottom() + params.bottomMargin + halfSize + transitionY;
                } else {
                    bounds.top = child.getTop() - params.topMargin - halfSize + transitionY;
                }
                bounds.bottom = bounds.top;
            }

            if (mPositionInsideItem) {
                if (isReverseLayout) {
                    bounds.top -= dividerSize;
                    bounds.bottom -= dividerSize;
                } else {
                    bounds.top += dividerSize;
                    bounds.bottom += dividerSize;
                }
            }
            return bounds;
        } else {
            //end edge
            return getDividerBound(position, parent, child);
        }
    }

    @Override
    public void getItemOffsets(Rect rect, View v, RecyclerView parent, RecyclerView.State state) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            super.getItemOffsets(rect, v, parent, state);
            return;
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL) {
            super.getItemOffsets(rect, v, parent, state);
            return;
        }

        int position = parent.getChildAdapterPosition(v);
        int groupIndex = getGroupIndex(position, parent);
        setItemOffsets(rect, groupIndex, parent);
    }

    @Override
    protected void setItemOffsets(Rect outRect, int position, RecyclerView parent) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            super.setItemOffsets(outRect, position, parent);
            return;
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL) {
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
        if (showDividerOnEdge) {
            if (spanIndex % spanCount == 0) {
                if (isReverseLayout(parent)) {
                    bottom += getDividerSize(position, parent);
                } else {
                    top += getDividerSize(position, parent);
                }
            } else if (spanIndex % spanCount == spanCount - 1) {
                if (isReverseLayout(parent)) {
                    top += getDividerSize(position, parent);
                } else {
                    bottom += getDividerSize(position, parent);
                }
            }
        }
        if (spanIndex % spanCount >= spanCount - 1) {
            //ignore the last span
            outRect.set(left, top, right, bottom);
            return;
        }
        if (isReverseLayout(parent)) {
            top += getDividerSize(position, parent);
        } else {
            bottom += getDividerSize(position, parent);
        }
        outRect.set(left, top, right, bottom);
    }

    @Override
    protected int getLastDividerOffset(RecyclerView parent) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return super.getLastDividerOffset(parent);
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL) {
            return super.getLastDividerOffset(parent);
        }
        return 1;
    }

    @Override
    protected int getGroupIndex(int position, RecyclerView parent) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return super.getGroupIndex(position, parent);
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL) {
            return super.getGroupIndex(position, parent);
        }
        return position;
    }

    @Override
    protected boolean wasDividerAlreadyDrawn(int position, RecyclerView parent) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return super.wasDividerAlreadyDrawn(position, parent);
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL) {
            return super.wasDividerAlreadyDrawn(position, parent);
        }
        return false;
    }

    public static class Builder extends HorizontalDividerItemDecoration.Builder {
        private boolean showDividerOnEdge;

        public Builder(Context context) {
            super(context);
        }

        public GridHorizontalDividerItemDecoration.Builder showDividerOnEdge() {
            showDividerOnEdge = true;
            return this;
        }

        @Override
        public GridHorizontalDividerItemDecoration build() {
            return new GridHorizontalDividerItemDecoration(this);
        }
    }
}
