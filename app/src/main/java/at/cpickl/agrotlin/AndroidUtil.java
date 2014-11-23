package at.cpickl.agrotlin;

import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class AndroidUtil {

    public static LayoutParams centered() {
        // accessing static stuff from kotlin is pain
        LayoutParams centerLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        centerLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        return centerLayout;
    }

}
