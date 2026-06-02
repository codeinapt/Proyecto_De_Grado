export interface User {
    id?: string;
    nombre_usuario: string;
    public_key: string;
    is_online?: boolean;
    fecha_registro?: Date;
}
