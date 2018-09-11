import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  image:any="./assets/imgs/face-detection.svg";
  constructor(public sanitizer: DomSanitizer,public navCtrl: NavController) {

  }
  start() {
    window['plugins'].start.camera(
      "", (result) => {
        let image = this.sanitizer.bypassSecurityTrustUrl("data:image/*;base64,"+result);
        this.image = image;
      },
      (err) => {
        console.log(err);
      },

    );
  }
}
