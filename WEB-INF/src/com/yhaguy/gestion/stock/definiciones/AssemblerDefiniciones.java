package com.yhaguy.gestion.stock.definiciones;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;

public class AssemblerDefiniciones extends Assembler{
	
	static final int LINEA = 0;
	static final int FAMILIA = 1;
	static final int PARTE = 2;
	static final int MARCA = 3;
	
	static final int AGREGAR = 0;
	static final int EDITAR = 1;
	
	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void gravarItemDefinicines(MyPair item, int tipo, int operacion)throws Exception{
		RegisterDomain rr = RegisterDomain.getInstance();
		
		System.out.println("====== gravarItemDefinicines() - ASSEMBLER : tipo "+tipo+"   operacion "+operacion);
		
		Tipo tipoDef = new Tipo();
		tipoDef.setSigla(item.getSigla());
		tipoDef.setDescripcion(item.getText());
		if(operacion == EDITAR){
			tipoDef.setId(item.getId());
		}		
		switch (tipo) {
			case LINEA:
				tipoDef.setTipoTipo(rr
						.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_ARTICULO_LINEA));
				break;
			case FAMILIA:
				tipoDef.setTipoTipo(rr
						.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_ARTICULO_FAMILIA));
				break;
			case PARTE:
				tipoDef.setTipoTipo(rr
						.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_ARTICULO_PARTE));
				break;
			case MARCA:
				tipoDef.setTipoTipo(rr
						.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_ARTICULO_MARCA));
				break;
		}
		rr.saveObject(tipoDef, getLogin());	
		
	}

}
