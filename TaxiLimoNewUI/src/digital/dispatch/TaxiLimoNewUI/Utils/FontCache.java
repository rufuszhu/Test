package digital.dispatch.TaxiLimoNewUI.Utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ezhang on 1/20/2015.
 */
//TL-369 to solve the memory leak issue for custom font set
public class FontCache {
    private static Map<String, Typeface> fontMap = new HashMap<String, Typeface>();

    public static Typeface getFont(Context context, String fontName){
        if (fontMap.containsKey(fontName)){
            return fontMap.get(fontName);
        }
        else {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), fontName);
            fontMap.put(fontName, tf);
            return tf;
        }
    }
}
