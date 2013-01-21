//
//  WelcomeViewController.m
//  Banda Beat
//
//  Created by Iker Mendilibar on 05/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "WelcomeViewController.h"
#import "BLAPI.h"

#define IS_IPHONE_5 ( fabs( ( double )[ [ UIScreen mainScreen ] bounds ].size.height - ( double )568 ) < DBL_EPSILON )

@interface WelcomeViewController ()
@property(strong,nonatomic) BLAPI *api;
@end

@implementation WelcomeViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
     self.trackedViewName = @"Welcome";
    
    _api = [BLAPI sharedInstance];
    if (!IS_IPHONE_5) {
        _background.image = [UIImage imageNamed:@"portada"];
    } else {
        _background.image = [UIImage imageNamed:@"portada_5"];
    }

}

-(void)viewWillAppear:(BOOL)animated
{
    if(_api.isLoginDataLoaded == TRUE) {
        [self performSegueWithIdentifier:@"TabBarSegue" sender:nil];
    }
}

-(IBAction)login:(id)sender
{
    [self performSegueWithIdentifier:@"LoginSegue" sender:nil];
}

@end
