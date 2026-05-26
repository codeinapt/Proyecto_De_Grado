export interface Gateway {
    id_dispositivo: string;
    bateria_nivel?: number;
    ultima_sincronizacion?: Date;
    ubicacion_lat?: number;
    ubicacion_lng?: number;
}
