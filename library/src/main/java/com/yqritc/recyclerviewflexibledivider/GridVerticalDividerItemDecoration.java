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
        bounds.top = child.getTop() + mMarginProvider.dividerTopMargin(position, parent) + transitionY;
        bounds.bottom = child.getHeight() - mMarginProvider.dividerBottomMargin(position, parent) + transitionY;
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
            bounds.top = child.getTop() + mMarginProvider.dividerTopMargin(position, parent) + transitionY;
            bounds.bottom = child.getHeight() - mMarginProvider.dividerBottomMargin(position, parent) + transitionY;
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
        } else {
            //end edge
            return getDividerBound(position, parent, child);
        }
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

        if (mPositionInsideItem) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();
        int spanIndex = spanSizeLookup.getSpanIndex(position, spanCount);
        if (showDividerOnEdge) {
            if (spanIndex % spanCount == 0) {
                if (isReverseLayout(parent)) {
                    outRect.set(0, 0, getDividerSize(position, parent), 0);
                } else {
                    outRect.set(getDividerSize(position, parent), 0, 0, 0);
                }
            } else if (spanIndex % spanCount == spanCount - 1) {
                if (isReverseLayout(parent)) {
                    outRect.set(getDividerSize(position, parent), 0, 0, 0);
                } else {
                    outRect.set(0, 0, getDividerSize(position, parent), 0);
                }
            }
        }
        if (spanIndex % spanCount >= spanCount - 1) {
            //ignore the last span
            return;
        }
        if (isReverseLayout(parent)) {
            outRect.set(getDividerSize(position, parent), 0, 0, 0);
        } else {
            outRect.set(0, 0, getDividerSize(position, parent), 0);
        }
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
    protected int getGroupIndex(int position, RecyclerView parent) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return super.getGroupIndex(position, parent);
        }
        if (((GridLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
            return super.getGroupIndex(position, parent);
        }
        return position;
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
    }
}
