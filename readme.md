pronto agregare las imagenes ... wait

# \***\*INSTALACIÓN EN\*\*WINDOWS\*\***

\***\*INSTALACIÓN DE BASE DE DATOS POSTGRESQL\*\***

Descargar versión actual de PostgreSQL para Windows desde [<u><span class="colour" style="color:rgb(0, 0, 255)"><u>https://www.enterprisedb.com/downloads/postgres-postgresql-downloads</u></span></u>](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads) Y ejecutar el instalador.

1. ![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps1.jpg)
Presionar Siguiente.
2.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps2.jpg)
Seleccionar la carpeta de instalación y presionar Siguiente.

3.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps3.jpg)
Seleccionar PostgreSQL Server, seleccionar pgAdmin4, Command Line Tools y presionar Siguiente.

4.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps4.jpg)
Seleccionar la carpeta donde se guardaran los datos y presionar siguiente.

5.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps5.jpg)
Ingresar la contraseña del superusuario y presionar siguiente.

6.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps6.jpg)
Ingresar el puerto desde donde se accede a la base de datos y presionar Siguiente.

7.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps7.jpg)
Seleccionar la configuración regional por defecto y presionar siguiente.

8.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps8.jpg)
Verificar los parámetros de instalación y presionar siguiente.

9.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps9.jpg)
Presionar siguiente para instalar.

## \***\*HABILITAR ACCESO A IP LOCALES\*\***

En la carpeta donde se almacenaran los datos 4) se debe editar el archivo pg\_hba.conf para indicar desde cuales ip locales pueden acceder a la base de datos, por defecto solo se tiene acceso desde la misma maquina osa 127.0.0.1.

## \***\*PRUEBA DE ACCESO A LA BASE DE DATOS\*\***

pgAdmin es el cliente por defecto por defecto de PostgreSql para acceder a los datos.

10.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps10.jpg)
Buscar la aplicación y ejecutar pgAdmin.

11.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps11.jpg)
Nos pedirá una nueva clave(password) para acceder de forma segura a pgAdmin, ingrese una clave y presione OK.

12.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps12.jpg)
Presionar en “Add New Server” para registrar un nuevo servidor de base de datos y seleccionar un nombre para dicho servidor.

13.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps13.jpg)
Cambiar a la pestaña “connection”, ingresar la ip 127.0.0.1 en el “Host Name/Address”, ingresar la clave que uso en 5) y presione Save.

14.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps14.jpg)
Desde esta aplicación en databases se debe crear la base de datos <b>**impges**</b>. Después mediante esta misma aplicación se puede hacer respaldo y restauración de la base de datos.
<br>
## \***\*IMPRENTAGES\*\***

\***\*DESCRIPCIÓN GENERAL\*\***

<b>**ImprentaGes**</b>es una aplicación de escritorio para Windows que permite gestionar ordenes de trabajo, boletas, facturas, productos, cajas, usuarios, permisos de acceso, etc., fue pensada para ser usada en imprentas, pero eso no impide que se pueda usar en otro tipo de negocio (Nota: la aplicación no implementa facturación electrónica).

Fue desarrollada en java 1.8 y se distribuye libremente como 3 proyectos de netbeans 8.2: ImprentaGes (La aplicación misma), KreadiJDBC (Utilidades para acceder a bases de datos) y KreadiSwing (Utilidades para las interfaces de usuario). \***\*EJECUTAR LA APLICACIÓN\*\***

Para ejecutar la aplicación necesita tener instalado Java 1.8, el ejecutable para Windows esta en
ImprentaGes/portables/ImprentaGes.exe

También se puede ejecutar la aplicación desde netbeans, pero tiene que tener los 3 proyectos abiertos: ImprentaGes, KreadiJDBC y KreadiSwing.

\***\*MANUAL DE USO\*\***

Al ingresar por primera vez deberá realizar la configuración inicial.

15.

![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps15.jpg)
Si la base de datos esta en otro equipo debe indicar la ip de ese equipo en Servidor, en clave debe ingresar la que configuro en 5) y en Caja debe indicar caja1 inicialmente.

Luego deberá autenticarse. 16)
![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps16.jpg)
Inicialmente para la cuenta de administración el usuario es admin y clave esta vacía.

A continuación se muestran las ventanas de aplicación y se explican los componentes de estas ventanas.

17. Ventana Principal.
![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps17.jpg)
18. Gestión de ordenes de trabajo, boletas y facturas.
19. Gestión de la caja.
20. Gestión de precios/productos.
21. Configuración general.
22. Información de la hora de acceso, el usuario y la caja.
23. Gestión del acceso.
24. Búsqueda por fechas, nombre, rut, numero de OT, numero de boleta, numero de factura. También permite realizar auditoria de movimientos entre fechas y auditoria con búsqueda de texto. También permite visualizar un resumen diario de los movimientos por cada caja.
25. Filtro de documentos terminados, pendientes o anulados.
26. Botón para ejecutar la búsqueda.
27. Resultados de la búsqueda que pueden ser exportados a Excel.
28. Configuración general.
![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps18.jpg)
29. Selección del tipo de documento.
30. Edición del formato de impresión del documento.
31. Renombrar el tipo de documento.
32. Crear un nuevo tipo de documento.
33. Eliminar un tipo de documento.
34. Configuración de otros parámetros: números automáticos, números iniciales, formatos de fecha/hora, IVA, cantidad de registros.
35. Username del usuario.
36. Nombre real del usuario.
37. Correo del usuario.
38. Teléfonos del usuario.
39. Permiso para editar Precios/Productos.
40. Permiso para editar la configuración.
41. Permiso para gestionar la caja.
42. Permiso para gestionar OT, boletas y facturas.
43. Indica si el usuario esta activo.
44. Muestra los usuarios desactivados también.
45. Renombrar el usarname del usuario seleccionado.
46. Cambiar la clave del usuario seleccionado.
47. Crear un usuario nuevo.
48. Nombre de la caja.
49. Configuración de la impresora de facturas para la caja seleccionada.
50. Configuración de la impresora de boletas para la caja seleccionada.
51. Configuración de la impresora de OT para la caja seleccionada.
52. Configuración de la impresora de Informes para la caja seleccionada.
53. Lista de usernames separados por espacios, de los usuarios que pueden acceder a la caja seleccionada.
54. Indica si la caja esta activa.
55. Muestra las cajas desactivadas también.
56. Renombrar la caja seleccionada.
57. Agregar una nueva caja.
58. Edición del formato de impresión del documento
![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps19.jpg)
59. Cantidad de caracteres de ancho del documento a imprimir.
60. Cantidad de caracteres de alto del documento a imprimir.
61. Código ascii de caracteres iniciales que se imprimirán.
62. Código ascii de caracteres finales que se imprimirán.
63. Texto que se imprimirá.
64. Posición actual del cursor.
65. Importar formato de impresión.
66. Exportar formato de impresión.
67. Guardar formato de impresión.
68. Gestión de Precios/Productos
![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps20.jpg)
69. Guardar modificaciones.
70. Agregar un nuevo producto.
71. Multiplicar por un factor los productos seleccionados.
72. Clonar los productos seleccionados.
73. Ordenar alfabéticamente.
74. Exportar a Excel.
75. Eliminar los productos seleccionados.
76. Detalle de los productos.
77. Precios de los productos.
78. Gestión de cajas.
![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps21.jpg)
79. Nombre del usuario autenticado que realizara la operación en la caja.
80. Nombre de la caja.
81. Fecha actual.
82. Turno diario de la caja (un turno ocurre entre la apertura y cierre de una caja).
83. Acciones: abrir caja, cerrar caja, ingresar dinero, retirar dinero.
84. Monto en efectivo al abrir caja y cerrar caja.
85. Monto en cheques al cerrar caja.
86. Monto en transferencias al cerrar caja.
87. Observaciones de la operación a realizar.
88. Cuadratura de dinero pagado.
89. Tipo de pagos(efectivo, cheques y transferencias).
90. Cantidad calculada de cada monto, que debe coincidir con lo contado al cerrar la caja.
91. Hora actual.
92. Botón para realizar la operación en la caja.
93. Edicion de orden de trabajo.
![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps22.jpg)
94. Retroceder hacia la orden de trabajo anterior.
95. Numero de orden de trabajo.
96. Avanzar hacia la orden de trabajo siguiente.
97. Clonar o anular OT.
98. Estado de la OT: Pendiente (No pagada), Terminada (Pagada/impresa factura o boleta) o Anulada.
99. “”
100. Numero de la boleta o Factura correspondiente.
101. Fecha de la solicitud de la OT.
102. Fecha de entrega de la OT.
103. Rut del cliente.
104. Nombre del cliente.
105. Dirección del cliente.
106. Comuna del cliente.
107. Ciudad de cliente.
108. Detalle de la OT.
109. Observaciones sobre la OT.
110. Botón para pagar OT.
111. Botón para imprimir OT.
112. Total a pagar.
113. Ingreso de pagos.
![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps23.jpg)
114. Total a pagar.
115. Saldo por pagar.
116. Cantidad a Pagar.
117. Cantidad ingresada a caja.
118. Calculo del vuelto.
119. Tipo de pago: Efectivo, cheque o transferencia.
120. Numero de documento.
121. Banco.
122. Botón para ingresar un pago.
123. Detalle de los pagos realizados.
124. Guardar los pagos ingresados.
125. Cuando se paga el total se puede generar e imprimir la boleta o factura.
![](file:///C:%5CUsers%5Ctomis%5CAppData%5CLocal%5CTemp%5Cksohtml16696%5Cwps24.jpg)

- - -

TODO LIST

CHEQUEAR SINCRONIZACION DE TIEMPO CON EQUIPO SERVIDOR

BUGS:

2. Al cambiar el foco a otra celda en un KTable... definir una coerencia en la posicion del cursor.