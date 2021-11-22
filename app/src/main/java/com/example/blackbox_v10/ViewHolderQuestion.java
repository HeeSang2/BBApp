package com.example.blackbox_v10;

import android.animation.ValueAnimator;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ViewHolderQuestion extends  RecyclerView.ViewHolder {

    TextView qus_title;
    TextView anwser_box;
    LinearLayout linearlayout;

    OnViewHolderItemClickListener onViewHolderItemClickListener;


    public ViewHolderQuestion(@NonNull View itemView) {
        super(itemView);

        qus_title = itemView.findViewById(R.id.qus_title);
        anwser_box = itemView.findViewById(R.id.anwser_box);
        linearlayout = itemView.findViewById(R.id.linearlayout);

        linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewHolderItemClickListener.onViewHolderItemClick();
            }
        });
    }

    public void onBind(DataQuestion data, int position, SparseBooleanArray selectedItems){
        qus_title.setText(data.getTitle());
        anwser_box.setText(data.getAnwser());
        changeVisibility(selectedItems.get(position));
    }

    private void changeVisibility(final boolean isExpanded) {
        // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
        ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, 600) : ValueAnimator.ofInt(600, 0);
        // Animation이 실행되는 시간, n/1000초
        va.setDuration(500);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // imageView의 높이 변경
                anwser_box.getLayoutParams().height = (int) animation.getAnimatedValue();
                anwser_box.requestLayout();
                // imageView가 실제로 사라지게하는 부분
                anwser_box.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }
        });
        // Animation start
        va.start();
    }

    public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
        this.onViewHolderItemClickListener = onViewHolderItemClickListener;
    }
}