package ar.uba.fi.mercadolibre.filter;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input)) return null;
        } catch (NumberFormatException ignored) {
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        if (b > a) return c >= a && c <= b;
        return c >= b && c <= a;
    }
}
