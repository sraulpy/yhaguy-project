package com.yhaguy.gestion.compras.definiciones;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.RegisterDomain;

public class AssemblerArticuloGastoDefiniciones extends Assembler{

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		throw new Exception(
				"No usar este método: AssemblerArticuloGastoDefiniciones.dtoToDomain");
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		throw new Exception(
				"No usar este método: AssemblerArticuloGastoDefiniciones.domainToDto");
	}
		
	/**
	 * obtiene la lista de articulo gasto..
	 */
	public List<MyArray> getArticulosGastosAss() throws Exception{
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloGasto> list = rr.getArticulosGastosRG();
		List<MyArray> listaArticulosGastos = new ArrayList<MyArray>();
		for(ArticuloGasto articuloGasto : list){
			MyArray articuloGastoMyArray = new MyArray();
			articuloGastoMyArray.setPos1(articuloGasto.getDescripcion());
			articuloGastoMyArray.setPos2(articuloGasto.getCreadoPor());
			articuloGastoMyArray.setPos3(articuloGasto.getVerificadoPor());
			articuloGastoMyArray.setPos5(new MyPair(articuloGasto.getCuentaContable().getId(), 
					articuloGasto.getCuentaContable().getDescripcion()));
			if(!articuloGasto.getVerificadoPor().equals("")){
				articuloGastoMyArray.setPos6(true);
			}else{
				articuloGastoMyArray.setPos6(false);
			}
			articuloGastoMyArray.setPos7(articuloGasto.getId()); // id articulo gasto
			listaArticulosGastos.add(articuloGastoMyArray);
		}
		return listaArticulosGastos;
	}
	/**
	 * guarda articulo gasto; lo utilizan agragar, modificar y verificar
	 * @param articuloGastoASS
	 * @param agregar; si es true crea uno nuevo, si no, tanto en modificar como en verificar actualiza el objeto
	 * @throws Exception
	 */
	
	public void saveArticuloGastoASS(MyArray articuloGastoASS, boolean agregar) throws Exception{
		RegisterDomain rr = RegisterDomain.getInstance();
		ArticuloGasto articuloGastoRG;
		MyPair ctaContable  = (MyPair) articuloGastoASS.getPos5();		
				
		articuloGastoRG = new ArticuloGasto();
		articuloGastoRG.setDescripcion(articuloGastoASS.getPos1().toString());
		articuloGastoRG.setCreadoPor(articuloGastoASS.getPos2().toString());
		articuloGastoRG.setVerificadoPor(articuloGastoASS.getPos3().toString());
		articuloGastoRG.setCuentaContable((CuentaContable) rr.getObject(CuentaContable.class.getName(), ctaContable.getId()));
		if(!agregar){
			articuloGastoRG.setId((Long) articuloGastoASS.getPos7());
		}
		rr.saveArticuloGastoRG(articuloGastoRG, this.getLogin());
	}
}
