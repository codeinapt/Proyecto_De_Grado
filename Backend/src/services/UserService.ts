import { UserRepository } from '../repositories/UserRepository';
import { User } from '../models/UserModel';

export class UserService {
    // Instanciamos el repositorio para poder usar sus funciones
    private userRepository: UserRepository;

    constructor() {
        this.userRepository = new UserRepository();
    }

    // Método principal del negocio: Registrar un nuevo usuario
    async registerUser(nombre_usuario: string, public_key: string): Promise<User> {

        // REGLA DE NEGOCIO 1: No pueden haber dos usuarios con el mismo nombre
        const existingUser = await this.userRepository.findByUsername(nombre_usuario);

        if (existingUser !== null) {
            // Si el usuario ya existe, cortamos la ejecución y lanzamos un error
            throw new Error(`El nombre de usuario '${nombre_usuario}' ya está en uso.`);
        }

        // REGLA DE NEGOCIO 2: Si todo está bien, construimos el objeto usuario
        const newUser: User = {
            nombre_usuario: nombre_usuario,
            public_key: public_key
        };

        // Y le pedimos al repositorio que lo guarde en la BD
        const savedUser = await this.userRepository.createUser(newUser);

        return savedUser;
    }
}
