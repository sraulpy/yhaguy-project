package com.yhaguy.util.macros;

import java.util.Date;

import com.coreweb.Config;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.extras.macros.BuscadorMacro;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoCtaCte;

public class BuscadorBancoChequeTercero extends BuscadorMacro {

	private static String CAMPO_FECHA = "fecha";
	private static String CAMPO_BANCO = "banco.descripcion";
	private static String CAMPO_NRO_CHEQUE = "numero";
	private static String CAMPO_LIBRADO = "librado";
	private static String CAMPO_MONEDA = "moneda.descripcion";
	private static String CAMPO_MONTO = "monto";
	private static String CAMPO_SUCURSALAPP = "sucursalApp.nombre";

	@Override
	public String getCampoDefault() {
		return CAMPO_NRO_CHEQUE;
	}

	@Override
	public String getAtributo() {
		return "Cheque";
	}

	@Override
	public Class getClaseObject() {
		return BancoChequeTercero.class;
	}

	@Override
	public String[][] getPosicionesCampos() {

		String[][] campos = { { CAMPO_FECHA, "Fecha...", Config.TIPO_DATE },
				{ CAMPO_BANCO, "Banco...", Config.TIPO_STRING },
				{ CAMPO_NRO_CHEQUE, "Nro. Cheque...", Config.TIPO_STRING },
				{ CAMPO_LIBRADO, "Librado...", Config.TIPO_STRING },
				{ CAMPO_MONEDA, "Moneda...", Config.TIPO_STRING },
				{ CAMPO_MONTO, "MONTO...", Config.TIPO_NUMERICO },
				{ CAMPO_SUCURSALAPP, "Sucursal...", Config.TIPO_STRING } };
		return campos;
	}

	@Override
	public String getTitulo() {
		return "Cheques de Terceros";
	}

	@Override
	public Assembler getAssembler() {
		return  null;
	}

}