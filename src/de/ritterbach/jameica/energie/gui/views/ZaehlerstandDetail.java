package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.action.ZaehlerstandDeleteAction;
import de.ritterbach.jameica.energie.gui.control.ZaehlerstandControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;

public class ZaehlerstandDetail extends AbstractView {

	public ZaehlerstandDetail() {
	}

	@Override
	public void bind() throws Exception {
		// draw the title
		GUI.getView().setTitle(Settings.i18n().tr("Zaehlerstand details"));
		// instanciate controller
		final ZaehlerstandControl control = new ZaehlerstandControl(this);
		Container c = new SimpleContainer(getParent());
		// layout with 2 columns
		ColumnLayout columns = new ColumnLayout(c.getComposite(), 2);

		// left side
		Container left = new SimpleContainer(columns.getComposite());
		left.addHeadline(control.getZaehlerName());
		left.addInput(control.getAbleseDatum());
		left.addInput(control.getAbleseWert());
		left.addInput(control.getVerbrauch());
		left.addInput(control.getIstSchaetzung());
		left.addInput(control.getIstZaehlerAusbau());
		left.addInput(control.getIstZaehlerEinbau());

		// right side
		Container right = new SimpleContainer(columns.getComposite(), true);
		right.addHeadline(Settings.i18n().tr("Notiz"));
		right.addInput(control.getNotiz());

		ButtonArea buttons = new ButtonArea();
		buttons.addButton(Settings.i18n().tr("Delete"), new ZaehlerstandDeleteAction(), control.getCurrentObject());
		buttons.addButton(Settings.i18n().tr("Store"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				control.handleStore();
			}
		}, null, true); // "true" defines this button as the default button
		// Don't forget to paint the button area
		buttons.paint(getParent());
	}
}
