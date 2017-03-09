package com.by122006library.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.by122006library.R;

public class CustomDialog extends Dialog {
	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private View contentView;
		private String message;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 *
		 * @param message title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 *
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 *
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setPositiveButton(
				OnClickListener listener) {
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(
				OnClickListener listener) {
			this.negativeButtonClickListener = listener;
			return this;
		}

		public CustomDialog show(){
			if(((Activity)context).isFinishing()) return null;
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(Looper.myLooper() != Looper.getMainLooper()) Looper.prepare();
			final CustomDialog dialog = new CustomDialog(context,
					 R.style.dialog);
			View layout = inflater.inflate( R.layout.custom_dialog_view, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById( R.id.title)).setText(title);
			// set the confirm button
			if (positiveButtonClickListener != null) {
				layout.findViewById( R.id.yes).setVisibility(View.VISIBLE);
				((ImageView) layout.findViewById( R.id.yes))
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								positiveButtonClickListener.onClick(dialog,
										DialogInterface.BUTTON_POSITIVE);
							}
						});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById( R.id.yes).setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonClickListener != null) {
				layout.findViewById( R.id.no).setVisibility(View.VISIBLE);
				((ImageView) layout.findViewById( R.id.no))
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								negativeButtonClickListener.onClick(dialog,
										DialogInterface.BUTTON_NEGATIVE);
							}
						});
				dialog.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						negativeButtonClickListener.onClick(dialog,
								DialogInterface.BUTTON_NEGATIVE);
					}

				});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.no).setVisibility(View.GONE);
			}
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((FrameLayout) layout.findViewById(R.id.context))
						.removeAllViews();
				((FrameLayout) layout.findViewById(R.id.context)).addView(
						contentView, new LayoutParams(LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));
			}
			dialog.setContentView(layout);
			dialog.show();
			if(Looper.myLooper() != Looper.getMainLooper()) Looper.loop();
			return dialog;
		}

	}
}