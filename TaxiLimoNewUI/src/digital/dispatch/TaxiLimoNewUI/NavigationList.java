package digital.dispatch.TaxiLimoNewUI;

import android.content.Context;

import digital.dispatch.TaxiLimoNewUI.Adapters.NavigationAdapter;
import digital.dispatch.TaxiLimoNewUI.Adapters.NavigationItemAdapter;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class NavigationList {

	public static NavigationAdapter getNavigationAdapter(Context context) {

		NavigationAdapter navigationAdapter = new NavigationAdapter(context);
		String[] menuItems = context.getResources().getStringArray(R.array.nav_menu_items);
		for (int i = 0; i < menuItems.length; i++) {

			String title = menuItems[i];

			NavigationItemAdapter itemNavigation = new NavigationItemAdapter(title, Utils.iconNavigation[i]);

			navigationAdapter.addItem(itemNavigation);

		}
		return navigationAdapter;
	}
}
