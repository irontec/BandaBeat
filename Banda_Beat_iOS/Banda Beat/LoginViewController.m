//
//  LoginViewController.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 22/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "LoginViewController.h"
#import "AppDelegate.h"
#import "LoginViewCell.h"
#import "ButtonViewCell.h"
#import "HTMLViewController.h"

@interface LoginViewController ()
@property(strong,nonatomic) BLAPI *api;
@end

@implementation LoginViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.trackedViewName = @"Login";
    
    _api = [BLAPI sharedInstance];
    [self.tableView setBackgroundColor:[UIColor clearColor]];
    [self.view setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"background"]]];

}

-(IBAction)about:(id)sender
{
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
    HTMLViewController *vc = [sb instantiateViewControllerWithIdentifier:@"HTMLViewController"];
    [vc setFileName:@"about"];
    [self presentModalViewController:vc animated:YES];
}


-(IBAction)cancelLogin:(id)sender
{
    [self dismissModalViewControllerAnimated:YES];
}

-(IBAction)newUser:(id)sender
{
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"http://www.bandabeat.com/#!/newuser"]];
}

-(IBAction)login:(id)sender
{
    NSString *username = [(LoginViewCell* )[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]] text].text;
    
    NSString *password = [(LoginViewCell* )[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:0]] text].text;
    
    [_api loginWithUsername:username password:password onCompletedHandler:^(Boolean isLogged, id methodResult) {
        
        if (isLogged) {
            
            NSDictionary *loginData = (NSDictionary*) methodResult;
            
            _api.token = [loginData objectForKey:@"token"];
            _api.userId = [loginData objectForKey:@"userid"];
            _api.username = [loginData objectForKey:@"username"];
            
             [self performSegueWithIdentifier:@"TabBarSegue" sender:nil];
            
            /*[_api checkPlaylistUpdateWithHandler:^(NSString *methodName, id methodResult) {
                NSString *result = methodResult;
                _api.generalToken = result;
                [_api saveLoginPreferences];
                [self performSegueWithIdentifier:@"TabBarSegue" sender:nil];
                
            }];*/
        } else {
            
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Errorea" message:methodResult delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
        }
    }];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
  
    LoginViewCell *cell;
        
    if (indexPath.row == 0) {
        cell = [tableView dequeueReusableCellWithIdentifier:@"UserCell"];
    } else if (indexPath.row == 1) {
        cell = [tableView dequeueReusableCellWithIdentifier:@"PasswordCell"];
    }
        
    return cell;
}


@end
