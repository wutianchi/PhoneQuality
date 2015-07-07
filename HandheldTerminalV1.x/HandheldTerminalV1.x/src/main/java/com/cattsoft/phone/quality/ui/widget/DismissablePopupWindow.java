package com.cattsoft.phone.quality.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.cattsoft.phone.quality.R;


public class DismissablePopupWindow extends android.widget.PopupWindow {
    Context ctx;
    View btnDismiss;
    // TextView lblText;
    View popupView;

    View popup_root;

    public DismissablePopupWindow(Context context, int layoutResource) {
        super(context);

        ctx = context;
        popupView = LayoutInflater.from(context).inflate(layoutResource, null);
        setContentView(popupView);

        btnDismiss = popupView.findViewById(R.id.dismiss);
        popup_root = popupView.findViewById(R.id.popup_root);
        if (null != popup_root) {
            popup_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        // lblText = (TextView)popupView.findViewById(R.id.terms_conditions);

        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);

        // Closes the popup window when touch outside of it - when looses focus
        setOutsideTouchable(true);
        setFocusable(true);

        // Removes default black background
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        setBackgroundDrawable(dw);
//        setBackgroundDrawable(new BitmapDrawable());
        if (null != btnDismiss) {
            btnDismiss.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        // Closes the popup window when touch it
        /*
		 * this.setTouchInterceptor(new View.OnTouchListener() {
		 *
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 *
		 * if (event.getAction() == MotionEvent.ACTION_MOVE) { dismiss(); }
		 * return true; } });
		 */
    } // End constructor

    // Attaches the view to its parent anchor-view at position x and y
    public void show(View anchor, int x, int y) {
        showAtLocation(anchor, Gravity.CENTER, x, y);
    }
}
