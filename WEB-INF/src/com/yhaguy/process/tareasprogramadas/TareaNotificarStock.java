package com.yhaguy.process.tareasprogramadas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.process.ProcesosArticulos;
import com.yhaguy.util.EnviarCorreo;

public class TareaNotificarStock {
	
	static final String[] SEND_YRPS = new String[] { "milvam@yhaguyrepresentaciones.com.py",
			"vgonzalez@yhaguyrepresentaciones.com.py", "administracion@yhaguyrepresentaciones.com.py",
			"mirala@yhaguyrepresentaciones.com.py" };
	
	static final String[] SEND_AUT = new String[] { "milvam@yhaguyrepresentaciones.com.py",
			"vgonzalez@yhaguyrepresentaciones.com.py", "administracion@yhaguyrepresentaciones.com.py",
			"lmeza@yhaguyrepresentaciones.com.py" };
	
	static final String[] SEND_GRP = new String[] { "vanesar@yhaguyrepuestos.com.py", "estelap@yhaguyrepuestos.com.py",
			"marcelov@yhaguyrepuestos.com.py" };
	
	/**
	 * notificacion por email de auditoria de stock..
	 */
	public static void enviarCorreoAuditoriaStock(long idFamilia) {	
		
		Map<Long, String> flias = new HashMap<Long, String>();
		flias.put((long) 1, ArticuloFamilia.FILTROS);
		flias.put((long) 2, ArticuloFamilia.LUBRICANTES);
		flias.put((long) 3, ArticuloFamilia.CUBIERTAS);
		flias.put((long) 11, ArticuloFamilia.BATERIAS_DISTRIBUIDOR);
		
		try {
			String texto = "Es necesario recalcular el stock de los siguientes códigos:";		
			
			List<Object[]> arts = new ArrayList<>();
			if (Configuracion.empresa.equals(Configuracion.EMPRESA_GRUPAUTO)) {
				arts = ProcesosArticulos.verificarStockGroupauto(2, idFamilia);
			} else {
				arts = ProcesosArticulos.verificarStock(2, idFamilia);
			}
			
			for (Object[] art : arts) {
				texto += "\n " + art[0];
			}
			
			if (arts.size() == 0) {
				texto += "\n (Todos los ítems estan correctos)";
			}			
			
			String[] send = SEND_GRP;
			String[] sendCCO = new String[] { "sergioa@yhaguyrepuestos.com.py" };
			String asunto = "Notificación Auditoría de Stock - " + Configuracion.empresa + " - " + flias.get(idFamilia);
			EnviarCorreo enviarCorreo = new EnviarCorreo();
			enviarCorreo.sendMessage(send, sendCCO, asunto, texto, "Notificacion.pdf", null);

		} catch (Exception e) {
			e.printStackTrace();
		}	
	}	
	
	public static void main(String[] args) {
		TareaNotificarStock.enviarCorreoAuditoriaStock(1);
		TareaNotificarStock.enviarCorreoAuditoriaStock(2);
		TareaNotificarStock.enviarCorreoAuditoriaStock(3);
		TareaNotificarStock.enviarCorreoAuditoriaStock(11);
	}
}
