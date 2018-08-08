package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class TipoMovimiento extends Domain {
	
	static final String VTA_CON = "VTA-CON";
	static final String VTA_CRE = "VTA-CRE";
	static final String NOT_CRE = "NOT-CRE";
	static final String TRF_MER = "TRF-MER";
	static final String REC_COB = "REC-COB";
	public static final String NC_CONTADO = "NCR-CON";
	public static final String NC_CREDITO = "NCR-CRE";

	private String descripcion;
	private String sigla;
	private String clase;
	
	private Tipo tipoIva;			//si es iva 10% - 5% - exenta	
	private Tipo tipoEmpresa;		//si el beneficiario es cliente, proveedor, funcionario o yhaguy
	private Tipo tipoOperacion;		//si la operacion corresponde a compra, venta, gasto, etc.
	private Tipo tipoComprobante;	//si el comprobante es interno o legal.
	private Tipo tipoDocumento; 	//El tipo de comprobante legal del movimiento
	
	/**
	 * @return el tipomovimiento abreviado
	 */
	public static String getAbreviatura(String sigla) {
		switch (sigla) {
		
		case Configuracion.SIGLA_TM_FAC_VENTA_CONTADO:
			return VTA_CON;
			
		case Configuracion.SIGLA_TM_FAC_VENTA_CREDITO:
			return VTA_CRE;
			
		case Configuracion.SIGLA_TM_TRANS_MERCADERIA:
			return TRF_MER;
			
		case Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA:
			return NOT_CRE;
		
		case Configuracion.SIGLA_TM_RECIBO_COBRO:
			return REC_COB;
		}		
		return "";
	}

	public String getDescripcion() {
		return descripcion.toUpperCase();
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Tipo getTipoIva() {
		return tipoIva;
	}

	public void setTipoIva(Tipo tipoIva) {
		this.tipoIva = tipoIva;
	}

	public Tipo getTipoEmpresa() {
		return tipoEmpresa;
	}

	public void setTipoEmpresa(Tipo tipoEmpresa) {
		this.tipoEmpresa = tipoEmpresa;
	}
	
	public Tipo getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(Tipo tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public Tipo getTipoComprobante() {
		return tipoComprobante;
	}

	public void setTipoComprobante(Tipo tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}
	
	public Tipo getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Tipo tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	@Override
	public int compareTo(Object o) {
		TipoMovimiento cmp = (TipoMovimiento) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}	
}
