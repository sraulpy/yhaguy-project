update importacionfacturadetalle d set costogs =
(select (r.tipocambio * d.costods) from importacionresumengastosdespacho r where p.resumengastosdespacho = r.id)
from importacionfactura f, importacionpedidocompra p where f.idimportacionpedidocompra = p.id and d.idfactura = f.id