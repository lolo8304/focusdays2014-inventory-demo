package com.lolo.focusdays.speechtotextdemo;

import org.robobinding.presentationmodel.PresentationModel;

import com.example.inventoryappbase.core.ean.EANPresentationModel;

@PresentationModel
public class Speech2TextPresentationModel {

	public EANPresentationModel eanModel;

	public EANPresentationModel getEanModel() {
		return eanModel;
	}

	public void setEanModel(EANPresentationModel eanModel) {
		this.eanModel = eanModel;
	}
}
