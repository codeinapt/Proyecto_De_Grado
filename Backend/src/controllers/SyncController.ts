import { Request, Response } from 'express';
import { MessageService } from '../services/MessageService';

export class SyncController {
    private messageService: MessageService;

    constructor() {
        this.messageService = new MessageService();
    }

    // Endpoint: POST /api/sync/up
    // Propósito: Recibir un arreglo de mensajes del Gateway
    async syncUp(req: Request, res: Response): Promise<void> {
        try {
            // 1. Extraemos los datos del JSON
            // Esperamos algo como: { "gateway_id": "GW-001", "mensajes": [...] }
            const { gateway_id, mensajes } = req.body;

            // Validación HTTP rápida
            if (!gateway_id || !mensajes) {
                res.status(400).json({ error: 'gateway_id y el arreglo de mensajes son obligatorios' });
                return;
            }

            // 2. Le pasamos el lote al servicio para limpiarlo y guardarlo
            const insertedCount = await this.messageService.processIncomingMessages(gateway_id, mensajes);

            // 3. Respondemos indicando éxito y cuántos se guardaron
            res.status(200).json({
                message: 'Sincronización hacia arriba (Sync UP) exitosa',
                mensajes_recibidos: mensajes.length,
                mensajes_guardados_nuevos: insertedCount
            });

        } catch (error: any) {
            // Si hubo algún error (ej. el JSON estaba mal formateado), devolvemos 400
            res.status(400).json({ error: error.message });
        }
    }

    // Endpoint: GET /api/sync/down/:gw_id
    // Propósito: El Gateway solicita mensajes pendientes desde la nube
    async syncDown(req: Request, res: Response): Promise<void> {
        try {
            const { gw_id } = req.params;

            if (!gw_id) {
                res.status(400).json({ error: 'El identificador del gateway es obligatorio' });
                return;
            }

            const pendingMessages = await this.messageService.getPendingMessages(gw_id);

            res.status(200).json({
                message: 'Sincronización hacia abajo (Sync DOWN) exitosa',
                gateway_id: gw_id,
                mensajes_pendientes: pendingMessages.length,
                mensajes: pendingMessages
            });
        } catch (error: any) {
            res.status(400).json({ error: error.message });
        }
    }
}
