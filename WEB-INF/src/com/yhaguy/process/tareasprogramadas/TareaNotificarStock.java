package com.yhaguy.process.tareasprogramadas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.Config;
import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Tarea_Programada;
import com.yhaguy.process.ProcesosArticulos;
import com.yhaguy.util.EnviarCorreo;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class TareaNotificarStock {
	
	static final String TAREA_NOTIFICAR_STOCK = "CORREO AUDITORIA STOCK ENVIADO - CUBIERTAS";
	static final String USER_NOTIF = "sys";
	static final String DIRECTORIO_BASE_MRA = "/home/mraserver/project/yhaguy-project/";
	static final String DIRECTORIO_BASE_BAT = "/home/server/project/yhaguy-baterias/";
	static final String DIRECTORIO_BASE_CEN = "/home/server/project/yhaguy-central/";
	static final String DIRECTORIO_BASE_RPS = "/home/server/project/yhaguy-baterias/";
	static final String DIRECTORIO_REPORTES_MRA = "/home/mraserver/project/yhaguy-project/reportes/";
	static final String DIRECTORIO_REPORTES_BAT = "/home/server/project/yhaguy-baterias/reportes/";
	static final String DIRECTORIO_REPORTES_CEN = "/home/server/project/yhaguy-central/reportes/";
	static final String DIRECTORIO_REPORTES_RPS = "/home/server/project/yhaguy-baterias/reportes/";
	static final String EMPRESA = Configuracion.EMPRESA_YRSA;
	static final String EMPRESA_BAT = Configuracion.EMPRESA_GTSA;
	static final String EMPRESA_RPS = Configuracion.EMPRESA_YRPS;
	
	static final String[] DESTINATARIOS_MRA = new String[] {
			"davida@yhaguyrepuestos.com.py", "soniat@yhaguyrepuestos.com.py" };
	
	static final String[] DESTINATARIOS_BAT = new String[] {
			"nataliac@gtsa.com.py", "dianaa@gtsa.com.py",
			"silviap@gtsa.com.py" };
	
	static final String[] DESTINATARIOS_CEN = new String[] { "soniat@yhaguyrepuestos.com.py",
			"oscarv@yhaguyrepuestos.com.py", "jorgeo@yhaguyrepuestos.com.py", "andream@yhaguyrepuestos.com.py" };
	
	static final String[] DESTINATARIOS_RPS = new String[] { "milvam@yhaguyrepuestos.com.py",
			"rosanag@yhaguyrepuestos.com.py", "oscarv@yhaguyrepuestos.com.py", "soniat@yhaguyrepuestos.com.py" };
	
	static final String[] COPIA_OCULTA = new String[] { "sergioa@yhaguyrepuestos.com.py" };
	static final String ASUNTO = "Auditoria de Stock - Yhaguy Repuestos S.A.";
	static final String ASUNTO_BAT = "Auditoria de Stock - Grupo Toyo S.A.";
	static final String ASUNTO_RPS = "Auditoria de Stock - Yhaguy Representaciones S.A.";
	
	/**
	 * notificacion por email de auditoria de stock..
	 */
	public static void enviarCorreoAuditoriaStock(String empresa, String directorioReportes, 
			String directorioBase, String[] destinatarios, String asunto) {
		Configuracion.empresa = empresa;
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_STOCK, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_STOCK);
				return;
			}
			
			System.out.println("Generando el Reporte..." + new Date());
			
			//List<Object[]> filtros = ProcesosArticulos.verificarStock(2, 1);
			//List<Object[]> lubricantes = ProcesosArticulos.verificarStock(2, 2);
			List<Object[]> cubiertas = ProcesosArticulos.verificarStock(2, 3);
			//List<Object[]> repuestos = ProcesosArticulos.verificarStock(2, 4);
			//List<Object[]> baterias = ProcesosArticulos.verificarStock(2, 5);
			
			List<Object[]> data = new ArrayList<Object[]>();
			//data.addAll(filtros);
			//data.addAll(lubricantes);
			data.addAll(cubiertas);
			//data.addAll(repuestos);
			//data.addAll(baterias);

			Config.DIRECTORIO_REAL_REPORTES = directorioReportes;
			Config.DIRECTORIO_BASE_REAL = directorioBase;
			
			ReporteAuditoriaStock rep = new ReporteAuditoriaStock();
			rep.setDatosReporte(data);
			rep.ejecutar(false);
			
			System.out.println("Reporte: " + directorioReportes + rep.getArchivoSalida());			
			
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_STOCK, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_STOCK);
				return;
			}
			
			System.out.println("enviando email..." + new Date());
			Tarea_Programada tarea = new Tarea_Programada();
			tarea.setFecha(new Date());
			tarea.setDescripcion(TAREA_NOTIFICAR_STOCK);
			
			EnviarCorreo correo = new EnviarCorreo(tarea);
			correo.sendMessage(destinatarios, COPIA_OCULTA, asunto,
					"Auditoria de Stock - Familia", "AuditoriaStock.pdf", directorioReportes + rep.getArchivoSalida());

		} catch (Exception e) {
			e.printStackTrace();
		}	
	}	
	
	public static void main(String[] args) {
		TareaNotificarStock.enviarCorreoAuditoriaStock(EMPRESA, DIRECTORIO_REPORTES_CEN, DIRECTORIO_BASE_CEN, DESTINATARIOS_CEN, ASUNTO);
		//TareaNotificarStock.enviarCorreoAuditoriaStock(EMPRESA_RPS, DIRECTORIO_REPORTES_RPS, DIRECTORIO_BASE_RPS, DESTINATARIOS_RPS, ASUNTO_RPS);
		//TareaNotificarStock.enviarCorreoAuditoriaStock(EMPRESA_BAT, DIRECTORIO_REPORTES_BAT, DIRECTORIO_BASE_BAT, DESTINATARIOS_BAT, ASUNTO_BAT);
	}
}

/**
 * Reporte de Auditoria de Stock..
 */
class ReporteAuditoriaStock extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Depósito", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Stock", TIPO_LONG, 25);
	static DatosColumnas col3 = new DatosColumnas("Historial", TIPO_LONG, 25);

	public ReporteAuditoriaStock() {
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Auditoría de Stock");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Stock-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
