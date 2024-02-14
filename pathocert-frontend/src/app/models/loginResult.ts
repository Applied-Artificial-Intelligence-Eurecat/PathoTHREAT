import { Links, Href } from './links';
import { Organization } from './organization';
import { User } from './user';

export class LoginResult {
    token: string;
    _links?: LinksLogin;
    user?: User;
    organization?: Organization;
}

export interface LinksLogin {
    href: string;
    org: LinksLogin;
    'my-user': Href;
}
