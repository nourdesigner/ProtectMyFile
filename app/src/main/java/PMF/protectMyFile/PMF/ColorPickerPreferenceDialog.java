package PMF.protectMyFile.PMF;

import PMF.protectMyFile.PMF.ColorPickerView.OnColorChangedListener;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class ColorPickerPreferenceDialog extends DialogPreference implements OnColorChangedListener{
	private int mColor = 0;

	public ColorPickerPreferenceDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			persistInt(mColor);
			// OU getSharedPreferences().edit().putInt(getKey(), mColor).commit();
		}

		super.onDialogClosed(positiveResult);
	}


	protected void onPrepareDialogBuilder(Builder builder) {
		int oldColor = getSharedPreferences().getInt(getKey(), Color.BLACK);
		builder.setView(new ColorPickerView(getContext(), this, oldColor));

		super.onPrepareDialogBuilder(builder);
	}


	public void colorChanged(int color) {
		mColor = color;
	}
}