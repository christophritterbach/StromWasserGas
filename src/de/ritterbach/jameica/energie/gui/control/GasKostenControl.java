package de.ritterbach.jameica.energie.gui.control;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.KostenGas;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextAreaInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class GasKostenControl extends AbstractControl {

	private KostenGas kosten;
	private DateInput gueltigVon;
	private DateInput gueltigBis;
	private DecimalInput grundpreis;
	private DecimalInput arbeitspreis;
	private DecimalInput faktor;
	private CheckboxInput istAbgerechnet;
	private CheckboxInput istRechnungsabschluss;
	private TextAreaInput notiz;

	public GasKostenControl(AbstractView view) {
		super(view);
	}

	private KostenGas getKostenGas() {
		if (kosten != null)
			return kosten;
		kosten = (KostenGas) getCurrentObject();
		return kosten;
	}

	public Input getGueltigVon() throws RemoteException {
		if (gueltigVon != null)
			return gueltigVon;

		Date gueltig = getKostenGas().getGueltigVon();
		if (gueltig == null)
			gueltig = new Date();
		this.gueltigVon = new DateInput(gueltig, Settings.DATEFORMAT);
		this.gueltigVon.setName(Settings.i18n().tr("Gueltig_von"));
		return this.gueltigVon;
	}

	public Input getGueltigBis() throws RemoteException {
		if (gueltigBis != null)
			return gueltigBis;

		Date gueltig = getKostenGas().getGueltigBis();
		if (gueltig == null)
			gueltig = new Date();
		this.gueltigBis = new DateInput(gueltig, Settings.DATEFORMAT);
		this.gueltigBis.setName(Settings.i18n().tr("Gueltig_bis"));
		return this.gueltigBis;
	}

	public Input getGrundpreis() throws RemoteException {
		if (grundpreis != null)
			return grundpreis;

		grundpreis = new DecimalInput(getKostenGas().getGrundpreis(), Settings.DECIMALFORMAT);
		grundpreis.setComment(Settings.i18n().tr("{0} pro Jahr", Settings.CURRENCY));
		grundpreis.setName(Settings.i18n().tr("Grundpreis"));
		grundpreis.isMandatory();
		grundpreis.setHint(Settings.i18n().tr("Grundpreis inkl. Steuern"));
		return this.grundpreis;
	}

	public Input getArbeitspreis() throws RemoteException {
		if (arbeitspreis != null)
			return arbeitspreis;

		arbeitspreis = new DecimalInput(getKostenGas().getArbeitspreis(), Settings.ARBEITSPREISFORMAT);
		arbeitspreis.setComment(Settings.i18n().tr("{0} pro kWh", Settings.CURRENCY));
		arbeitspreis.setName(Settings.i18n().tr("Arbeitspreis"));
		arbeitspreis.isMandatory();
		arbeitspreis.setHint(Settings.i18n().tr("Arbeitspreis inkl. Steuern"));
		return this.arbeitspreis;
	}

	public Input getFaktor() throws RemoteException {
		if (faktor != null)
			return faktor;

		faktor = new DecimalInput(getKostenGas().getFaktor(), Settings.ARBEITSPREISFORMAT);
		faktor.setComment(Settings.i18n().tr("kWh_pro_qm"));
		faktor.setName(Settings.i18n().tr("Faktor"));
		faktor.isMandatory();
		faktor.setHint(Settings.i18n().tr("Faktor_hint"));
		return this.faktor;
	}

	public Input getIstAbgerechnet() throws RemoteException {
		if (istAbgerechnet != null)
			return istAbgerechnet;

		istAbgerechnet = new CheckboxInput(getKostenGas().isAbgerechnet());
		istAbgerechnet.setComment(Settings.i18n().tr("Rechnung wurde erstellt"));
		istAbgerechnet.setName(Settings.i18n().tr("Abgerechnet"));
		return this.istAbgerechnet;
	}

	public Input getIstRechnungsabschluss() throws RemoteException {
		if (istRechnungsabschluss != null)
			return istRechnungsabschluss;

		istRechnungsabschluss = new CheckboxInput(getKostenGas().isRechnungsabschluss());
		istRechnungsabschluss.setComment(Settings.i18n().tr("Ende der Abrechnungsperiode"));
		istRechnungsabschluss.setName(Settings.i18n().tr("Rechnungsabschluss"));
		return this.istRechnungsabschluss;
	}

	public Input getNotiz() throws RemoteException {
		if (notiz != null)
			return notiz;
		notiz = new TextAreaInput(getKostenGas().getNotiz());
		notiz.setName("");
		return notiz;
	}

	public void handleStore() {
		try {
			KostenGas kost = getKostenGas();
			kost.setGueltigVon((Date) getGueltigVon().getValue());
			kost.setGueltigBis((Date) getGueltigBis().getValue());
			Double gp = (Double) getGrundpreis().getValue();
			kost.setGrundpreis(gp == null ? new BigDecimal(0) : BigDecimal.valueOf(gp));
			Double ap = (Double) getArbeitspreis().getValue();
			kost.setArbeitspreis(ap == null ? new BigDecimal(0) : BigDecimal.valueOf(ap));
			Double fakt = (Double) getArbeitspreis().getValue();
			kost.setFaktor(fakt == null ? new BigDecimal(1) : BigDecimal.valueOf(fakt));
			kost.setAbgerechnet((Boolean) getIstAbgerechnet().getValue());
			kost.setRechnungsabschluss((Boolean) getIstRechnungsabschluss().getValue());
			kost.setNotiz((String) getNotiz().getValue());
			try {
				kost.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("kosten stored successfully"), StatusBarMessage.TYPE_SUCCESS));
			} catch (ApplicationException e) {
				Application.getMessagingFactory()
						.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
			}
		} catch (RemoteException e) {
			Logger.error("error while storing kosten", e);
			Application.getMessagingFactory().sendMessage(
					new StatusBarMessage(Settings.i18n().tr("error while storing kosten: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}
}