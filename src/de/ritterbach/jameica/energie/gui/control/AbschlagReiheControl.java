package de.ritterbach.jameica.energie.gui.control;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Abschlag;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AbschlagReiheControl extends AbstractControl {

	private DateInput abschlagDatumAnfang;
	private DateInput abschlagDatumEnde;
	private DecimalInput abschlagBetrag;

	public AbschlagReiheControl(AbstractView view) {
		super(view);
	}

	public Input getAbschlagDatumAnfang() throws RemoteException {
		if (abschlagDatumAnfang != null)
			return abschlagDatumAnfang;

		Date datum = new Date();
		this.abschlagDatumAnfang = new DateInput(datum, Settings.DATEFORMAT);
		this.abschlagDatumAnfang.setName(Settings.i18n().tr("Abschlag_Datum_Anfang"));
		return this.abschlagDatumAnfang;
	}

	public Input getAbschlagDatumEnde() throws RemoteException {
		if (abschlagDatumEnde != null)
			return abschlagDatumEnde;

		Date datum = new Date();
		this.abschlagDatumEnde = new DateInput(datum, Settings.DATEFORMAT);
		this.abschlagDatumEnde.setName(Settings.i18n().tr("Abschlag_Datum_Ende"));
		return this.abschlagDatumEnde;
	}

	public Input getAbschlagBetrag() throws RemoteException {
		if (abschlagBetrag != null)
			return abschlagBetrag;

		abschlagBetrag = new DecimalInput(BigDecimal.ZERO, Settings.DECIMALFORMAT);
		abschlagBetrag.setName(Settings.i18n().tr("Abschlag_Betrag"));
		return this.abschlagBetrag;
	}

	public Zaehler getZaehlerName() {
		return Settings.getZaehler();
	}

	public void handleStore() {
		try {
			Date datum = (Date) getAbschlagDatumAnfang().getValue();
			Calendar cal = Calendar.getInstance();
			cal.setTime(datum);
			boolean isMonatsLetzter = (cal.getActualMaximum(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH));

			Date endeDatum = (Date) getAbschlagDatumEnde().getValue();
			Double ab = (Double) getAbschlagBetrag().getValue();
			BigDecimal betrag = (ab == null ? new BigDecimal(0) : BigDecimal.valueOf(ab));
			while (!datum.after(endeDatum)) {
				Abschlag abschlag = (Abschlag) Settings.getDBService().createObject(Abschlag.class, null);
				abschlag.setZaehler(Settings.getZaehler());
				abschlag.setAbschlagDatum(datum);
				abschlag.setAbschlagbetrag(betrag);
				try {
					abschlag.store();
					Application.getMessagingFactory().sendMessage(new StatusBarMessage(
							Settings.i18n().tr("abschlag stored successfully"), StatusBarMessage.TYPE_SUCCESS));
				} catch (ApplicationException e) {
					Application.getMessagingFactory()
							.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
				}
				// Nächster Monat
				cal.add(Calendar.MONTH, 1);
				if (isMonatsLetzter)
					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				datum = cal.getTime();
			}
		} catch (RemoteException e) {
			Logger.error("error while storing abschlag", e);
			Application.getMessagingFactory()
					.sendMessage(new StatusBarMessage(
							Settings.i18n().tr("error while storing abschlag: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}
}
