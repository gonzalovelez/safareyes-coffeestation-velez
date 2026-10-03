SET search_path TO coffestation;

create table "usuario" (
    "id" bigint not null,
    "nombre" varchar(255) not null,
    "contraseña" varchar(255) not null,
    "rol" varchar(20) not null,

    primary key ("id"),

    check ("rol" in (
        'admin',
        'barista',
        'cliente'
    ))
);

create table "cliente" (
    "id" bigint not null,
    "nombre" varchar(255) not null,
    "email" varchar(255) not null unique,
    "dni" varchar(20) not null unique,
    "usuario_id" bigint not null unique,

    primary key ("id"),

    foreign key ("usuario_id")
        references "usuario"("id")
);

create table "categoria" (
    "id" bigint not null,
    "nombre" varchar(255) not null,
    "disponible" boolean not null,

    primary key ("id")
);

create table "producto" (
    "id" bigint not null,
    "nombre" varchar(255) not null,
    "descripcion" varchar(255) not null,
    "precio" decimal(10,2) not null,
    "activo" boolean not null,
    "disponible" boolean not null,
    "categoria_id" bigint not null,

    primary key ("id"),

    foreign key ("categoria_id")
        references "categoria"("id")
);

create table "alergeno" (
    "id" bigint not null,
    "nombre" varchar(255) not null,

    primary key ("id")
);

create table "alergeno_producto" (
    "alergeno_id" bigint not null,
    "producto_id" bigint not null,

    primary key ("alergeno_id", "producto_id"),

    foreign key ("alergeno_id")
        references "alergeno"("id"),

    foreign key ("producto_id")
        references "producto"("id")
);

create table "cupones" (
    "id" bigint not null,
    "codigo" varchar(255) not null,
    "tipo" varchar(20) not null,
    "valor" decimal(10,2) not null,
    "fecha_inicio" timestamp(0) without time zone not null,
    "fecha_fin" timestamp(0) without time zone not null,
    "max_usos" bigint not null,
    "importe_minimo" decimal(10,2),

    "cliente_id" bigint not null,

    primary key ("id"),

    unique ("codigo"),

    foreign key ("cliente_id")
        references "cliente"("id"),

    check ("tipo" in (
        'porcentaje',
        'importe'
    ))
);

create table "pedido" (
    "id" bigint not null,
    "numero_turno" bigint not null,
    "fecha_hora" timestamp(0) without time zone not null,
    "base" decimal(10,2) not null,
    "iva" decimal(10,2) not null,

    "cliente_id" bigint not null,

    primary key ("id"),

    foreign key ("cliente_id")
        references "cliente"("id")
);

create table "producto_pedido" (
    "producto_id" bigint not null,
    "pedido_id" bigint not null,
    "cantidad" bigint not null,

    primary key ("producto_id", "pedido_id"),

    foreign key ("producto_id")
        references "producto"("id"),

    foreign key ("pedido_id")
        references "pedido"("id")
);

create table "descuento" (
    "id" bigint not null,
    "importe" decimal(10,2) not null,
    "origen" varchar(255) not null,
    "pedido_id" bigint not null,

    primary key ("id"),

    foreign key ("pedido_id")
        references "pedido"("id")
);

create table "pedido_cupones" (
    "pedido_id" bigint not null,
    "cupon_id" bigint not null,
    "descuento" decimal(10,2) not null,

    primary key ("pedido_id", "cupon_id"),

    foreign key ("pedido_id")
        references "pedido"("id"),

    foreign key ("cupon_id")
        references "cupones"("id")
);