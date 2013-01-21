//
//  WelcomeViewController.h
//  Banda Beat
//
//  Created by Iker Mendilibar on 05/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GAITrackedViewController.h"

@interface WelcomeViewController : GAITrackedViewController
@property(weak,nonatomic) IBOutlet UIImageView *background;
-(IBAction)login:(id)sender;
@end
