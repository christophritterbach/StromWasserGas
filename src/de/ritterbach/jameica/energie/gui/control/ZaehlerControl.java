package de.ritterbach.jameica.energie.gui.control;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class ZaehlerControl extends AbstractControl {
	private Zaehler zaehler;
	private TextInput name;
	private TextInput messEinheit;
	private TextInput ableseEinheit;
	private CheckboxInput nutztFaktor;
	private CheckboxInput istAktiv;

	public ZaehlerControl(AbstractView view) {
		super(view);
	}

	private Zaehler getZaehler() {
		if (zaehler != null)
			return zaehler;
		zaehler = (Zaehler) getCurrentObject();
		return zaehler;
	}

	public Input getName() throws RemoteException {
		if (name != null)
			return name;
		this.name = new TextInput(getZaehler().getName(), 255);
		this.name.setMandatory(true);
		this.name.setName(Settings.i18n().tr("Zaehlername"));
		return this.name;
	}

	public CheckboxInput getIstAktiv() throws RemoteException {
		if (istAktiv != null)
			return istAktiv;
		this.istAktiv = new CheckboxInput(getZaehler().getIstAktiv());
		this.istAktiv.setName(Settings.i18n().tr("ist_aktiv"));
		return this.istAktiv;
	}
	
	public CheckboxInput getNutztFaktor() throws RemoteException {
		if (nutztFaktor != null)
			return nutztFaktor;
		this.nutztFaktor = new CheckboxInput(getZaehler().getNutztFaktor());
		this.nutztFaktor.setName(Settings.i18n().tr("nutzt_faktor"));
		return this.nutztFaktor;
	}
	
	public Input getMessEinheit() throws RemoteException {
		if (messEinheit != null)
			return messEinheit;
		this.messEinheit = new TextInput(getZaehler().getMessEinheit(), 10);
		this.messEinheit.setMandatory(true);
		this.messEinheit.setName(Settings.i18n().tr("Messeinheit"));
		this.messEinheit.setComment(Settings.i18n().tr("Messeinheit_Comment"));
		this.messEinheit.setHint(Settings.i18n().tr("Messeinheit_Hint"));
		return messEinheit;
	}

	public Input getAbleseEinheit() throws RemoteException {
		if (ableseEinheit != null)
			return ableseEinheit;
		this.ableseEinheit = new TextInput(getZaehler().getAbleseEinheit(), 10);
		this.ableseEinheit.setMandatory(true);
		this.ableseEinheit.setName(Settings.i18n().tr("Ableseeinheit"));
		this.ableseEinheit.setComment(Settings.i18n().tr("Ableseeinheit_Comment"));
		this.ableseEinheit.setHint(Settings.i18n().tr("Ableseeinheit_Hint"));
		return ableseEinheit;
	}

	public void handleStore() {
		try {
			Zaehler zaehler = getZaehler();

			zaehler.setName((String) getName().getValue());
			zaehler.setIstAktiv((Boolean) getIstAktiv().getValue());
			zaehler.setMessEinheit((String) getMessEinheit().getValue());
			zaehler.setAbleseEinheit((String) getAbleseEinheit().getValue());
			zaehler.setNutztFaktor((Boolean) getNutztFaktor().getValue());

			try {
				zaehler.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("zaehler stored successfully"), StatusBarMessage.TYPE_SUCCESS));
			} catch (ApplicationException e) {
				Application.getMessagingFactory()
						.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
			}
		} catch (RemoteException e) {
			Logger.error("error while storing zaehler", e);
			Application.getMessagingFactory().sendMessage(
					new StatusBarMessage(Settings.i18n().tr("Error while storing zaehler: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}

}
