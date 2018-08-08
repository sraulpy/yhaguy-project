package com.yhaguy.util.migracion;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CondicionPago;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;

public class CtaCteMigracion {
	private static RegisterDomain rr = RegisterDomain.getInstance();
	public static final String CONDICION_CONTADO = "Contado";
	public static final String CONDICION_CREDITO = "Credito";
	public static final String CONDICION_30DIAS = "30 Días";
	public static final String CONDICION_60DIAS = "60 Días";
	public static final String CONDICION_75DIAS = "75 Días";
	public static final String CONDICION_90DIAS = "90 Días";
	public static final String CONDICION_120DIAS = "120 Días";
	public static final String CONDICION_CHEQUE = "Cheque";
	public static final String ESTADO_HABILITADO = "Habilitado";
	public static final String ESTADO_BLOQUEADO = "Bloqueado";
	public static final String ESTADO_SOLICITUD = "Solicitud";
	public static final String ESTADO_SINCUENTA = "Sin cuenta";
	public static final String FACTURA_CONTADO = "CONTADO";
	public static final String FACTURA_CREDITO = "CREDITO";
	public static final String FACTURA_GASTO_CONTADO = "Gastos Contado";
	public static final String FACTURA_GASTO_CREDITO = "Gastos Credito";
	public static final String FACTURA_GASTO_IMPORTACION = "Gastos de Import.";	
	public static final String FACTURA_COMPRA_CREDITO = "Comp.  CREDITO";
	public static final String FACTURA_BASE_IMPONIBLE = "Base Imponible";
	public static final String MONEDA_USS = "US";
	public static final String MONEDA_GS = "GS";

	
	
	private static void grabarDB(Domain d) throws Exception {
		rr.saveObject(d, Configuracion.USER_SYSTEMA);
	}
	

	/**
	 * Obtiene la condicion de pago correspondiente al sistema ovevete de
	 * acuerdo a la condicion de pago proveniente de los datos a migrar.
	 * Obs.: 
	 * 		1- Si el String no posee ninguna correspondencia con el sistema
	 * 		actual se guarda la condicion de pago contado por defecto.
	 * 		2- Las condiciones "75 dias", "120 dias " y "Cheque" no poseen 
	 * 		correspondencias en el sistema actual por lo tanto se asignara
	 * 		como condicion de pago "Otros" correspondiente al sistema ovevete.
	 * 
	 * @param condicion
	 *            Condicion proveniente de la migracion del tipo estring
	 *            correspondiente a las condiciones de pago del JediSoft.
	 * @return CondicionPago equivalente en el sistema ovevete
	 * @throws Exception
	 */
	public CondicionPago getCondicionPagoCliente(String condicion) throws Exception{

		CondicionPago condicionPago = new CondicionPago();
		switch (condicion) {
			case CONDICION_CONTADO:
				condicionPago = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CONTADO);
				break;
			case CONDICION_CREDITO:
				condicionPago = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CREDITO_30);
				break;
			case CONDICION_30DIAS:
				condicionPago = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CREDITO_30);
				break;
			case CONDICION_60DIAS:
				condicionPago = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CREDITO_60);
				break;
			case CONDICION_90DIAS:
				condicionPago = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CREDITO_90);
				break;
			case CONDICION_75DIAS:
				condicionPago = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_OTROS);
				break;
			case CONDICION_120DIAS:
				condicionPago = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_OTROS);
				break;
			case CONDICION_CHEQUE:
				condicionPago = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_OTROS);
				break;
			default:
				condicionPago = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CONTADO);
				break;
		}
		return condicionPago;
	}

	/**
	 * Retorna el tipo correspondinte de estado de cliente del sistema Ovevete
	 * de acuerdo a los parametros obtenidos de la migracion.
	 * 
	 * @param estado
	 *            String que se obtuvo de la migracion que hace referencia al
	 *            estado de la Cta. Cte.
	 * @return El Tipo de estado equivalente dentro del sistema Ovevete.
	 * @throws Exception
	 */
	public Tipo getEstadoComoClienteProveedor(String estado) throws Exception {

		Tipo estadoCliente = new Tipo();
		switch (estado) {
			case ESTADO_HABILITADO:
				estadoCliente = rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_ACTIVO);
				break;
			case ESTADO_BLOQUEADO:
				estadoCliente = rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_BLOQUEADO);
				break;
			case ESTADO_SINCUENTA:
				estadoCliente = rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA);
				break;
			case ESTADO_SOLICITUD:
				estadoCliente = rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA);
				break;		
			default:
				estadoCliente = rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA);
				break;
		}
		return estadoCliente;
	}
	
	/**
	 * Obtiene la CtaCteLineaCredito perteneciente al sistema Ovevete de acuerdo
	 * al limite de credito obtenido como informacion de migracion del sistema
	 * JediSoft
	 * Obs.:
	 * 		1- Si no existe una linea de credito equivalente al parametro "limiteCredito"
	 *	    almacenada en la BD entonces se captura ese Exception, la cual se trata, como 
	 *		el monto de la linea de credito es inexistente en la BD entonces se creara
	 *		una nueva "CtaCteLineaCredito" con dicho monto.
	 *		2- En el sistema JediSoft cuando la limite de credito es "1" significa que 
	 *		no posee una linea de credito, o que esta esta deshabilitada por lo tanto 
	 *		se retorna la "Sin Linea de Credito" perteneciente al sistema Ovevete. 
	 * 
	 * @param limiteCredito
	 *            Corresponde a limite obtenido de la migracion
	 * @return Linea de credito equivalente en el sistema Ovevete
	 * @throws Exception
	 * 			
	 */
	public CtaCteLineaCredito getLineaCreditoPorMonto(double limiteCredito) throws Exception {
		
		CtaCteLineaCredito linea = new CtaCteLineaCredito();		
		try{
			if(limiteCredito == 1){
				linea = rr.getCtaCteEmpresaLineaCreditoByMontoLinea(0);
			}else{
				linea = rr.getCtaCteEmpresaLineaCreditoByMontoLinea(limiteCredito);
			}
			
		}catch(Exception e){
			System.out.println("[CtaCteMigracion]: Linea"
								+ limiteCredito
								+ " en la BD inexistente se procedera a almacenarla como una nueva");
			linea.setLinea(limiteCredito);
			grabarDB(linea);
		}

		return linea;
		
	}
	
	/**
	 * Obtiene un "TipoMovimiento" correspondiente al sistema ovevete de acuerdo
	 * al String de tipo de factura proveniente de la informacion a migrar.
	 * 
	 * @param tipoFactura
	 *            String correspondiente al tipo de factura cuya
	 *            correspondencia se quiere hallar en el sistema Ovevete
	 * @return El tipo de movimiento equivalente dentro del sistema Ovevete
	 * @throws Exception
	 */
	public TipoMovimiento getTipoMovimientoPorDetFacturaVenta(String tipoFactura) throws Exception{
		TipoMovimiento tipoMovimiento = new TipoMovimiento();
		switch (tipoFactura) {
			case FACTURA_CONTADO:
				tipoMovimiento = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO);
				break;
			case FACTURA_CREDITO:
				tipoMovimiento = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
				break;
		}
		return tipoMovimiento;	
	}
	
	/**
	 * Obtiene el "TipoMovimiento" equivalente al sistema Ovevete de acuerdo al
	 * String concepto de la factura proveniente de la informacion a migrar.
	 * 
	 * @param concepto
	 *            String correspondiente al concepto de la factura cuya
	 *            correspondencia se quiere hallar en el sistema Ovevete
	 * @return Tipo de movimiento equivalente al parametro en el sistema Ovevete
	 * @throws Exception
	 */
	public TipoMovimiento getTipoMovimientoPorDetFacturaCompraGasto(String concepto) throws Exception{
		TipoMovimiento tipoMovimiento = new TipoMovimiento();
		switch (concepto) {
			case FACTURA_GASTO_CONTADO:
				tipoMovimiento = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO);
				break;
			case FACTURA_GASTO_CREDITO:
				tipoMovimiento = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO);
				break;
			case FACTURA_GASTO_IMPORTACION:
				tipoMovimiento = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_IMPORT_CREDITO);
				break;
			case FACTURA_COMPRA_CREDITO:
				tipoMovimiento = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO);
				break;
			case FACTURA_BASE_IMPONIBLE:
				tipoMovimiento = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO);
				break;
		}
		return tipoMovimiento;	
	}
	
	/**
	 * Obtiene la equivalencia de moneda del sistema Ovevete de acuerdo al
	 * String de moneda proveniente de los datos a migrar.
	 * 
	 * @param detMoneda
	 *            El string de la moneda de los datos a migrar
	 * @return El tipo moneda equivalente dentro del sistema Ovevete
	 * @throws Exception
	 */
	public Tipo getMoneda(String detMoneda) throws Exception{
		Tipo moneda = new Tipo();
		switch (detMoneda) {
			case MONEDA_GS:
				moneda = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
				break;
			case MONEDA_USS:
				moneda = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR);
				break;
			default:
				break;
		}
		return moneda;
	}	

}
