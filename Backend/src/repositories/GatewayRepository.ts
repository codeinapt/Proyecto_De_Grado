import pool from '../config/database';
import { Gateway } from '../models/GatewayModel';

export class GatewayRepository {
    
    // Método para registrar o actualizar el heartbeat del Gateway
    async upsertGateway(gateway: Gateway): Promise<Gateway> {
        // Usamos INSERT ... ON CONFLICT DO UPDATE
        // Esto permite que el mismo código sirva para registrar por primera vez
        // y para actualizar las siguientes veces, sin duplicar registros.
        const query = `
            INSERT INTO nodo_gateway (id_dispositivo, bateria_nivel, ultima_sincronizacion, ubicacion_lat, ubicacion_lng)
            VALUES ($1, $2, $3, $4, $5)
            ON CONFLICT (id_dispositivo) 
            DO UPDATE SET 
                bateria_nivel = EXCLUDED.bateria_nivel,
                ultima_sincronizacion = EXCLUDED.ultima_sincronizacion,
                ubicacion_lat = EXCLUDED.ubicacion_lat,
                ubicacion_lng = EXCLUDED.ubicacion_lng
            RETURNING *;
        `;

        const values = [
            gateway.id_dispositivo, 
            gateway.bateria_nivel, 
            gateway.ultima_sincronizacion, 
            gateway.ubicacion_lat, 
            gateway.ubicacion_lng
        ];

        const result = await pool.query(query, values);

        return result.rows[0];
    }
}
