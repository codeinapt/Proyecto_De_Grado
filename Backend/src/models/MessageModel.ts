// Definimos los estados posibles del mensaje tal cual están en PostgreSQL
export enum EstadoMensaje {
    EN_ESPERA = 'EN_ESPERA',
    TRANSITANDO = 'TRANSITANDO',
    RECIBIDO_EN_NUBE = 'RECIBIDO_EN_NUBE',
    ENTREGADO = 'ENTREGADO'
}

// Modelo de datos para la tabla Mensaje
export interface Message {
    uuid_mensaje: string; // Generado por el celular
    emisor_id: string; // UUID del usuario que envía
    receptor_id: string; // UUID del usuario que recibe
    contenido_cifrado: string; // El texto incomprensible (Base64)
    tipo_mensaje?: string; // Por defecto será TEXTO
    estado?: EstadoMensaje; // Por defecto será EN_ESPERA o RECIBIDO_EN_NUBE
    contador_saltos?: number; 
    fecha_emision_movil: Date; // Hora en la que el usuario le dio a "Enviar"
    fecha_servidor?: Date; // Hora en la que llegó a nuestro Node.js
}
