import path from 'path';
import { randomUUID } from 'crypto';
import { mkdir, writeFile } from 'fs/promises';
import { Multimedia } from '../models/MultimediaModel';
import { MultimediaRepository } from '../repositories/MultimediaRepository';

export class MultimediaService {
    private multimediaRepository: MultimediaRepository;
    private uploadRoot: string;

    constructor() {
        this.multimediaRepository = new MultimediaRepository();
        this.uploadRoot = path.join(process.cwd(), 'uploads', 'encrypted');
    }

    async saveEncryptedFile(
        mensaje_id: string,
        fileBuffer: Buffer,
        thumbnail_base64?: string
    ): Promise<Multimedia> {
        if (!mensaje_id) {
            throw new Error('mensaje_id es obligatorio');
        }

        if (!fileBuffer || fileBuffer.length === 0) {
            throw new Error('El archivo cifrado no puede estar vacío');
        }

        const messageExists = await this.multimediaRepository.messageExists(mensaje_id);
        if (!messageExists) {
            throw new Error('No existe un mensaje asociado para guardar la multimedia');
        }

        await mkdir(this.uploadRoot, { recursive: true });

        const fileName = `${mensaje_id}-${randomUUID()}.enc`;
        const absolutePath = path.join(this.uploadRoot, fileName);
        const storedPath = path.join('uploads', 'encrypted', fileName).replace(/\\/g, '/');

        await writeFile(absolutePath, fileBuffer);

        return this.multimediaRepository.create({
            mensaje_id,
            url_almacenamiento: storedPath,
            peso_bytes: fileBuffer.length,
            thumbnail_base64
        });
    }
}
