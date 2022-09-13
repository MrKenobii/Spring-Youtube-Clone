import { NgModule } from '@angular/core';
import { AuthModule } from 'angular-auth-oidc-client';


@NgModule({
    imports: [AuthModule.forRoot({
        config: {
            authority: 'https://dev-vcf1v0xv.us.auth0.com',
            redirectUrl: window.location.origin,
            clientId: '3d8Dujqu22zHOPB6O1Chf0aTmmG7BcwW',
            scope: 'openid profile offline_access',
            responseType: 'code',
            silentRenew: true,
            useRefreshToken: true,
        }
      })],
    exports: [AuthModule],
})
export class AuthConfigModule {}