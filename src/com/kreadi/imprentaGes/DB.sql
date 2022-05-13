# updateOTFacturacion =
update ordenes set rut_factura=? where numero=?;

# deleteOTFacturacion =
update ordenes set rut_factura=null where numero=?;

# getPagosDiario =
select * from (select a.tiempo, a.caja, b.nombre, concat('OT:',ot_numero,' PAGO ',tipo, case when tipo='EFECTIVO' then '' 
	else concat(' NUMERO:', a.numero,' BANCO:',a.banco,' SUBPAGO:',a.subpago) end, ' CLIENTE:', c.razon) as tipo, 
                case when tipo='EFECTIVO' then a.monto else 0 end as efectivo, 
                case when tipo='CHEQUE' then a.monto else 0 end as cheque,
                case when tipo='TRANSFERENCIA' then a.monto else 0 end as transferecia
from pagos a, usuarios b, ordenes c where a.usuario=b.usuario and a.ot_numero=c.numero and aprobado union
select a.tiempo, a.caja, b.nombre, 
concat ('ID:', a.id, case when a.estado='A' then ' APERTURA CAJA' else 
case when a.estado='I' then ' INGRESO CAJA' else case when a.estado='E' then ' EGRESO CAJA' else 
concat(' CIERRE CAJA ',a.efectivo,' ',a.cheques,' ',a.transferencias) end end end)  as type, 
a.efectivo_contabilizado as efectivo, a.cheques_contabilizado as cheque, a.transferencias_contabilizado as transferencia  
from turnos a, usuarios b where a.usuario=b.usuario) a
where a.tiempo>=? AND a.tiempo< ? + 86400000 order by 1;

# getTurnoAudit =
select b.nombre, a.caja, a.dia, a.turno, a.estado, a.efectivo, a.cheques, 
    a.transferencias, a.efectivo_contabilizado, a.cheques_contabilizado, 
    a.transferencias_contabilizado, a.observaciones, a.tiempo
from turnos a, usuarios b where a.usuario=b.usuario and a.id=?;

# getLastId =
select lastval();

# countParams =
select count(*) from parametros;

# setParam =
INSERT INTO PARAMETROS (parametro, valor) VALUES (?,?);

# setPass2 =
update usuarios set clave=? where usuario=?;

# setPass =
update usuarios set clave=? where usuario=? and clave=?;

# getAbonoSaldo =
select sum(monto) from pagos where ot_numero=?;

# setOTENTREGA =
update ordenes set fecha_entrega=extract(epoch from now())*1000 where numero=?;

# getParametro =
select valor from parametros where parametro=?;

# updateParam =
update parametros set valor=? where parametro=?;

# getParametros =
select parametro, valor from parametros order by 1;

# updateFormato =
update formatos set  ancho=?, alto=?, startCode=?, endCode=?, text=? where nombre=?;

# getFormato =
select ancho, alto, startCode, endCode, text from formatos where nombre=?;

# removeFormato =
delete from formatos where nombre=?;

# newFormato =
insert into formatos (nombre, ancho, alto, startCode, endCode, text) values (?, 80, 40, '', '', '');

# renameFormato =
update formatos set nombre=? where nombre=?;

# getFormatoNames =
select nombre  from formatos order by 1;

# getTurnoApertura =
select efectivo_contabilizado from turnos where dia=? and caja=? and turno=? and estado='A';

# getIngresoEgreso =
select sum(efectivo_contabilizado), sum(cheques_contabilizado), sum(transferencias_contabilizado)
from turnos where dia=? and caja=? and turno=? and estado in ('I', 'E');

# getCuadratura =
select tipo, sum(monto) from pagos where dia=? and caja=? and turno=? and aprobado group by tipo;

# existDocumento =
SELECT numero from ordenes where documento=?;

# getOTDiaria =
SELECT a.numero, documento, fecha_solicitud,  fecha_entrega, a.razon, estado, total,  (select sum(c.monto) from pagos c where c.ot_numero=a.numero and c.aprobado)
FROM ordenes a
where  ((a.fecha_solicitud>=? AND a.fecha_solicitud< ? + 86400000) or (a.fecha_entrega>=? AND a.fecha_entrega< ? + 86400000))
group by a.numero order by 1 desc;

# insertAuditoria =
INSERT INTO auditoria (tiempo, usuario, caja,  texto, subtexto) values (extract(epoch from now())*1000,?,?,?,?);

# getAuditoriaDiaria =
SELECT a.tiempo, a.caja, b.nombre, a.texto, a.subtexto FROM auditoria a, usuarios b where a.usuario=b.usuario and a.tiempo>=? AND a.tiempo< ? + 86400000 order by 1 desc;

# existeOT =
select true from ordenes where numero=?;

# existDocumentoBancario =
select ot_numero from pagos where tipo=? and numero=? and subpago=? and banco=?;

# setDocumento =
UPDATE ordenes set documento=?, fecha_documento=? where numero=?;

# nextFactura =
select max(documento) from ordenes where ESTADO in ('T','A');

# nextBoleta =
select -min(documento) from ordenes where ESTADO in ('T','A');

# isCancelado =
select total - select sum(monto) from pagos where OT_NUMERO;

# getPagoTotal =
select sum(monto) from pagos where OT_NUMERO=?;

# removeNoAprovPago =
DELETE FROM pagos WHERE ot_numero=? and not aprobado;

# aprovPagoPrev =
SELECT tiempo, tipo, numero, subpago, banco, monto from pagos where ot_numero=? and aprobado=false; 

# aprovPago =
UPDATE pagos SET aprobado=true where ot_numero=? and aprobado=false;

# delPago =
DELETE FROM pagos WHERE id=?;

# getPagos =
SELECT a.tiempo, a.tipo, a.numero, a.subpago, a.banco, a.monto, a.caja, b.nombre, a.aprobado, a.id
FROM pagos a, usuarios b WHERE a.usuario=b.usuario and a.OT_NUMERO=? order by a.tiempo;

# insertPago =
INSERT INTO pagos(ot_numero, tipo, numero, subpago, banco, monto, tiempo, caja, dia, turno, usuario, turno_id, aprobado)
VALUES (?, ?, ?, ?, ?, ?, extract(epoch from now())*1000, ?, ?, ?, ?, ?, ?);

# setOTESTADO =
UPDATE ordenes SET estado=? WHERE numero=?;

# selectOT =
SELECT estado,  documento,  fecha_solicitud, fecha_entrega, rut, neto, iva, total, observaciones,
razon, direccion, comuna, ciudad, sii, telefonos, celulares, mail, web, fantasia, giro, sucursal, (SELECT count(*)  FROM PAGOS WHERE ot_numero=?), rut_factura, fecha_documento  FROM ordenes where numero=?;

# selectClienteRut =
SELECT razon, direccion, comuna, ciudad, sii, telefonos, celulares, mail, web, fantasia, giro, sucursal FROM clientes where rut=?;

# selectClienteRazon =
SELECT rut, direccion, comuna, ciudad, sii, telefonos, celulares, mail, web, fantasia, giro, sucursal FROM clientes where razon=?;

# selectDetalle =
SELECT idx, producto, folio, cantidad, precio, total FROM detalles WHERE ot_numero=? order by idx;

# insertDetalle =
INSERT INTO detalles(ot_numero, idx, producto, folio, cantidad, precio, total) VALUES (?, ?, ?, ?, ?, ?, ?);

# delDetalle =
DELETE FROM detalles WHERE ot_numero=?;

# insertCliente =
INSERT INTO clientes(rut, razon, direccion, comuna, ciudad, sii, telefonos, celulares, mail, web, fantasia, giro, sucursal)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

# updateCliente =
UPDATE clientes SET razon=?, direccion=?, comuna=?, ciudad=?, sii=?, telefonos=?, celulares=?, mail=?, web=?,
fantasia=?, giro=?, sucursal=? WHERE rut=?;

# getCliente =
SELECT razon, direccion, comuna, ciudad, sii, telefonos, celulares, mail, web, fantasia, giro, sucursal FROM clientes WHERE rut=?;

# getRutFacturacion =
SELECT rut_factura from ordenes where numero=?;

# existCliente =
SELECT rut FROM clientes WHERE rut=?;

# getRutFromRazon =
SELECT rut FROM clientes WHERE razon=?;

# updateOT =
UPDATE ordenes SET rut=?, fecha_solicitud=?, fecha_entrega=?, estado=?,  observaciones=?, total=?, neto=?, iva=?, usuario=?,
modificado=extract(epoch from now())*1000, razon=?, direccion=?, comuna=?, ciudad=?, sii=?, telefonos=?, 
celulares=?, mail=?, web=?, fantasia=?, giro=?, sucursal=? WHERE numero=?;

# insertOT =
INSERT INTO ordenes(numero, rut, fecha_solicitud, fecha_entrega, estado, observaciones, total, neto, iva, usuario, modificado, caja,
 dia, turno, turno_id,razon, direccion, comuna, ciudad, sii, telefonos, celulares, mail, web, fantasia, giro, sucursal) VALUES 
(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, extract(epoch from now())*1000, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

# getLastOT =
SELECT max(numero) from ordenes;

# getPrevOT =
SELECT max(numero) from ordenes where numero<?;

# getNextOT =
SELECT min(numero) from ordenes where numero>?;

# getPrecio =
select precio from precios where producto=?;

# getDBTime =
select extract(epoch from now())*1000;

# getLastTurno2 =
select dia, turno, id from
turnos where caja=? order by dia desc,  turno desc limit 1;

# getCajaTurno =
select  turno+1, case when estado='A' then 1 else 0 end, dia from turnos where caja=? and estado not in('E','I')  order by tiempo desc limit 1;

# insertTurno =
INSERT INTO turnos(caja, dia, turno, usuario, estado, efectivo, cheques, transferencias, tiempo, observaciones,
efectivo_contabilizado, cheques_contabilizado, transferencias_contabilizado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, extract(epoch from now())*1000, ?, ?, ?, ?);

# delPrecios(1) =
delete from precios;

# insertPrecios(1) =
insert into precios(id,producto, precio) values (?,?,?);

# getPrecios =
select producto, precio from precios order by id;

# login =
select 1 from usuarios where activo and  usuario=? and clave=?;

# userInfo =
select nombre, permiso_precios, permiso_config, permiso_caja, permiso_ot, activo from usuarios where usuario=?;

# existUsuario =
select 1 from usuarios where usuario = ? limit 1;

# existCaja =
select 1 from cajas where caja = ? limit 1;

# getUsuariosCaja =
select usuarios from cajas where caja = ? limit 1;

# selectUsuarios =
SELECT usuario, nombre, email, telefonos, permiso_precios, permiso_config, permiso_caja, permiso_ot, activo FROM usuarios order by activo desc, usuario;

# selectCajas =
SELECT caja, impresora_factura, impresora_boleta, impresora_ot, impresora_informe, usuarios, activa  FROM cajas order by activa desc, caja;

# selectCaja =
SELECT impresora_factura, impresora_boleta, impresora_ot, impresora_informe, usuarios, activa  FROM cajas where caja=?;

# updateCaja =
UPDATE cajas  SET caja=?, impresora_factura=?, impresora_boleta=?, impresora_ot=?, impresora_informe=?, usuarios=?, activa=? WHERE caja=?;

# insertCaja =
INSERT INTO cajas(caja, impresora_factura, impresora_boleta,  impresora_ot,  impresora_informe, usuarios, activa) VALUES (?, ?,? ,?, ?, ?, ?);

# updateUsuario =
UPDATE usuarios  SET usuario=?, nombre=?, email=?, telefonos=?, permiso_precios=?, permiso_config=?, permiso_caja=?, permiso_ot=?, activo=?  WHERE usuario=?;

# insertUsuario =
INSERT INTO usuarios(usuario, nombre, clave, email, telefonos, permiso_precios, permiso_config, permiso_caja, permiso_ot, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

# createDB =
------------------------------------------------------
DROP TABLE IF EXISTS cajas;
CREATE TABLE cajas (
    caja VARCHAR(32) NOT NULL,
    impresora_factura VARCHAR(128),
    impresora_boleta VARCHAR(128),
    impresora_ot VARCHAR(128),
    impresora_informe VARCHAR(128),
    usuarios VARCHAR(256),
    activa BOOLEAN,
    PRIMARY KEY (caja)
);

DROP TABLE IF EXISTS clientes;
CREATE TABLE clientes (
    rut INTEGER,
    razon VARCHAR(128) UNIQUE,
    direccion VARCHAR(128),
    comuna VARCHAR(128),
    ciudad VARCHAR(128),
    sii VARCHAR(128),
    telefonos VARCHAR(128),
    celulares VARCHAR(128),
    mail VARCHAR(128),
    web VARCHAR(128),
    fantasia VARCHAR(128),
    giro VARCHAR(1024),
    sucursal VARCHAR(1024),
    PRIMARY KEY (rut)
);

DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
    usuario VARCHAR(32) NOT NULL,
    nombre VARCHAR(64),
    clave VARCHAR(64) NOT NULL,
    email VARCHAR(64),
    telefonos VARCHAR(64),
    permiso_precios BOOLEAN,
    permiso_config BOOLEAN,
    permiso_caja BOOLEAN,
    permiso_ot BOOLEAN,
    activo BOOLEAN NOT NULL,
    PRIMARY KEY (usuario)
);

DROP TABLE IF EXISTS turnos;
CREATE TABLE turnos (
    id bigserial,
    caja VARCHAR(32) NOT NULL,
    dia INTEGER NOT NULL,
    turno INTEGER NOT NULL,
    usuario VARCHAR(32),
    estado CHARACTER(1) NOT NULL,
    efectivo INTEGER,
    cheques INTEGER,
    transferencias INTEGER,
    tiempo BIGINT,
    observaciones TEXT,
    efectivo_contabilizado INTEGER,
    cheques_contabilizado INTEGER,
    transferencias_contabilizado INTEGER,
    PRIMARY KEY (caja, dia, turno, id),
    CONSTRAINT turno_caja_fkey FOREIGN KEY (caja) REFERENCES cajas(caja),
    CONSTRAINT turno_usuario_fkey FOREIGN KEY (usuario) REFERENCES usuarios(usuario)
);

DROP TABLE IF EXISTS ordenes;
CREATE TABLE ordenes (
    numero INTEGER NOT NULL,
    rut INTEGER,
    rut_factura INTEGER,
    fecha_solicitud BIGINT,
    fecha_entrega BIGINT,
    fecha_documento BIGINT,
    estado CHARACTER(1),
    observaciones TEXT,
    total INTEGER,
    documento INTEGER,-- mayor que cero es factura, menor es boleta
    neto INTEGER,
    iva INTEGER,
    modificado BIGINT,
    usuario VARCHAR(32),
    caja VARCHAR(32),
    dia INTEGER,
    turno INTEGER,
    turno_id bigint,-- se cambio el turno_estado... por el id
    razon VARCHAR(128),
    direccion VARCHAR(128),
    comuna VARCHAR(128),
    ciudad VARCHAR(128),
    sii VARCHAR(128),
    telefonos VARCHAR(128),
    celulares VARCHAR(128),
    mail VARCHAR(128),
    web VARCHAR(128),
    fantasia VARCHAR(128),
    giro VARCHAR(1024),
    sucursal VARCHAR(1024),
    PRIMARY KEY (numero),
    CONSTRAINT ordenes_usuario_fkey FOREIGN KEY (usuario) REFERENCES usuarios(usuario),
    CONSTRAINT ordenes_caja_fkey FOREIGN KEY (caja, dia, turno, turno_id) REFERENCES turnos(caja, dia, turno, id)
);

DROP TABLE IF EXISTS detalles;
CREATE TABLE detalles (
    ot_numero INTEGER NOT NULL,
    idx INTEGER NOT NULL,
    producto VARCHAR(128),
    folio INTEGER,
    cantidad INTEGER,
    precio INTEGER,
    total INTEGER,
    PRIMARY KEY (ot_numero, idx),
    CONSTRAINT detalle_ot_numero_fkey FOREIGN KEY (ot_numero) REFERENCES ordenes(numero)
);

DROP TABLE IF EXISTS precios;
CREATE TABLE precios (
    id INTEGER,
    producto VARCHAR(128) NOT NULL,
    precio INTEGER,
    PRIMARY KEY (producto)
);

DROP TABLE IF EXISTS pagos;
CREATE TABLE pagos (
    id bigserial,
    ot_numero INTEGER NOT NULL,
    tiempo BIGINT,
    tipo VARCHAR(16),
    numero VARCHAR(64),
    subpago VARCHAR(2),
    banco VARCHAR(64),
    monto INTEGER,
    caja VARCHAR(32) NOT NULL,
    dia INTEGER NOT NULL,
    turno INTEGER NOT NULL,
    turno_id bigint NOT NULL,
    usuario VARCHAR(32) NOT NULL,
    aprobado BOOLEAN,
    PRIMARY KEY (id),
    CONSTRAINT pagos_ot_numero_fkey FOREIGN KEY (ot_numero) REFERENCES ordenes(numero),
    CONSTRAINT pagos_usuario_fkey FOREIGN KEY (usuario) REFERENCES usuarios(usuario),
    CONSTRAINT pagos_caja_fkey FOREIGN KEY (caja, dia, turno, turno_id) REFERENCES turnos(caja, dia, turno, id)
);

DROP TABLE IF EXISTS auditoria;
CREATE TABLE auditoria (
    tiempo BIGINT,
    usuario VARCHAR(64),
    caja VARCHAR(64),
    texto  VARCHAR(64),
    subtexto VARCHAR(512),
    PRIMARY KEY (tiempo, usuario, caja)
);

DROP TABLE IF EXISTS parametros;
CREATE TABLE parametros (
    parametro VARCHAR(32) NOT NULL,
    valor VARCHAR(128),
    PRIMARY KEY (parametro)
);

DROP TABLE IF EXISTS formatos;
CREATE TABLE formatos (
    nombre VARCHAR(32) NOT NULL,
    ancho INTEGER,
    alto INTEGER,
    startCode VARCHAR(512),
    endCode VARCHAR(512),
    text TEXT,
    PRIMARY KEY (nombre)
);

--------------------------------------------------------------------

DROP INDEX IF EXISTS texto_auditoria_idx;
CREATE INDEX texto_auditoria_idx ON auditoria (texto);

DROP INDEX IF EXISTS clientes_ciudad_idx;
CREATE INDEX clientes_ciudad_idx ON clientes(ciudad);

DROP INDEX IF EXISTS clientes_comuna_idx;
CREATE INDEX clientes_comuna_idx ON clientes (comuna);

DROP INDEX IF EXISTS clientes_giro_idx;
CREATE INDEX clientes_giro_idx ON clientes (giro);

DROP INDEX IF EXISTS clientes_razon_idx;
CREATE INDEX clientes_razon_idx ON clientes (razon);

DROP INDEX IF EXISTS clientes_sii_idx;
CREATE INDEX clientes_sii_idx ON clientes (sii);

DROP INDEX IF EXISTS fki_turno_usuario_fkey;
CREATE INDEX fki_turno_usuario_fkey ON turnos (usuario);

DROP INDEX IF EXISTS ordenes_estado_idx;
CREATE INDEX ordenes_estado_idx ON ordenes (estado);

DROP INDEX IF EXISTS ordenes_documento_boleta_idx;
CREATE INDEX ordenes_documento_boleta_idx ON ordenes (documento);

DROP INDEX IF EXISTS ordenes_fecha_entrega_idx;
CREATE INDEX ordenes_fecha_entrega_idx ON ordenes (fecha_entrega);

DROP INDEX IF EXISTS ordenes_fecha_solicitud_idx;
CREATE INDEX ordenes_fecha_solicitud_idx ON ordenes (fecha_solicitud);

DROP INDEX IF EXISTS ordenes_rut_idx;
CREATE INDEX ordenes_rut_idx ON ordenes (rut);

DROP INDEX IF EXISTS pagos_numero_banco_idx;
CREATE INDEX pagos_numero_banco_idx ON pagos (numero, subpago, banco);

DROP INDEX IF EXISTS pagos_ot_numero_idx;
CREATE INDEX pagos_ot_numero_idx ON pagos (ot_numero);

DROP INDEX IF EXISTS pagos_tipo_idx;
CREATE INDEX pagos_tipo_idx ON pagos (tipo);

DROP INDEX IF EXISTS precios_id_idx;
CREATE INDEX precios_id_idx ON precios (id);


DROP INDEX IF EXISTS turno_id_idx;
CREATE INDEX turno_id_idx ON turnos (id);


--------------------------------------------------------------------

INSERT INTO USUARIOS (USUARIO, NOMBRE, CLAVE, PERMISO_PRECIOS, PERMISO_CONFIG, PERMISO_CAJA, PERMISO_OT, ACTIVO) VALUES ('admin', 'Administrador', '', true, true, true, true, true);
INSERT INTO CAJAS (caja, usuarios, activa) VALUES ('caja1','',true);
INSERT INTO PARAMETROS (parametro, valor) VALUES ('BOLETA AUTOMATICO','v');
INSERT INTO PARAMETROS (parametro, valor) VALUES ('BOLETA INICIO','1');
INSERT INTO PARAMETROS (parametro, valor) VALUES ('FACTURA AUTOMATICO','v');
INSERT INTO PARAMETROS (parametro, valor) VALUES ('FACTURA INICIO','1');
INSERT INTO PARAMETROS (parametro, valor) VALUES ('FORMATO FECHA','EEE dd-MMM-yy');
INSERT INTO PARAMETROS (parametro, valor) VALUES ('FORMATO FECHA HORA','dd-MMM-yyyy HH:mm:ss');
INSERT INTO PARAMETROS (parametro, valor) VALUES ('ITEMS ORDEN','12');
INSERT INTO PARAMETROS (parametro, valor) VALUES ('IVA','19');
INSERT INTO PARAMETROS (parametro, valor) VALUES ('OT INICIO','1');
