package digital.dispatch.TaxiLimoNewUI.Utils;

import digital.dispatch.TaxiLimoNewUI.R;
import android.content.Context;

public class Utils {

	//Set all the navigation icons and always to set "zero 0" for the item is a category
	public static int[] iconNavigation = new int[] { 
		R.drawable.ic_action_about, R.drawable.ic_action_about, R.drawable.ic_action_about,
		R.drawable.ic_action_about};	
	
	//get title of the item navigation
	public static String getTitleItem(Context context, int posicao){		
		String[] titulos = context.getResources().getStringArray(R.array.nav_menu_items);  
		return titulos[posicao];
	} 
	
}
